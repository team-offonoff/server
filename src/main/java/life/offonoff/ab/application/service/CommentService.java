package life.offonoff.ab.application.service;

import life.offonoff.ab.application.service.common.TextUtils;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.VoteRepository;
import life.offonoff.ab.repository.comment.CommentRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static life.offonoff.ab.application.service.common.LengthInfo.COMMENT_CONTENT;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TopicRepository topicRepository;
    private final VoteRepository voteRepository;

    //== find ==//
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    public Slice<CommentResponse> findAll(Long memberId, Long topicId, Pageable pageable) {

        checkMemberCanViewComments(memberId, topicId);

        Member member = findMemberFetchLikedComments(memberId);
        return commentRepository.findAll(topicId, pageable)
                .map(comment -> CommentResponse.from(comment, member));
    }

    private void checkMemberCanViewComments(Long memberId, Long topicId) {
        if (!topicRepository.existsByIdAndActiveTrue(topicId)) {
            throw new TopicNotFoundException(topicId);
        }

        if (!voteRepository.existsByVoterIdAndTopicId(memberId, topicId)) {
            throw new UnableToViewCommentsException(topicId);
        }
    }

    private void checkTopicCanBeCommented(Long topicId) {
        if (!topicRepository.existsByIdAndActiveTrue(topicId)) {
            throw new TopicNotFoundException(topicId);
        }
    }

    private Member findMember(final Long memberId) {
        return memberRepository.findByIdAndActiveTrue(memberId)
                .orElseThrow(() -> new MemberByIdNotFoundException(memberId));
    }

    private Member findMemberFetchLikedComments(final Long memberId) {
        return memberRepository.findByIdFetchLikedComments(memberId)
                .orElseThrow(() -> new MemberByIdNotFoundException(memberId));
    }

    private Topic findTopic(Long topicId) {
        return topicRepository.findByIdAndActiveTrue(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
    }

    private Vote findVote(Long memberId, Long topicId) {
        return voteRepository.findByVoterIdAndTopicId(memberId, topicId)
                .orElseThrow(() -> new MemberNotVoteException(memberId, topicId));
    }

    //== save ==//
    @Transactional
    public CommentResponse register(Long memberId, CommentRequest request) {
        Member member = findMember(memberId);
        Topic topic = findTopic(request.getTopicId());

        validateContent(request.getContent());

        Comment comment = new Comment(member, topic, request.getContent());
        commentRepository.save(comment);

        return CommentResponse.from(comment);
    }

    private void validateContent(String content) {
        final int length = TextUtils.countGraphemeClusters(content);
        if (length > COMMENT_CONTENT.getMaxLength() || length < COMMENT_CONTENT.getMinLength()) {
            throw new LengthInvalidException("댓글 내용", COMMENT_CONTENT);
        }
    }

    //== like ==//
    @Transactional
    public void likeCommentForMember(final Long memberId, final Long commentId, final Boolean enable) {
        Member liker = findMember(memberId);
        Comment comment = findById(commentId);

        if (enable) {
            doLike(liker, comment);
            return;
        }
        cancelLike(liker, comment);
    }

    private void doLike(Member liker, Comment comment) {
        liker.likeCommentIfNew(comment);
    }

    private void cancelLike(Member liker, Comment comment) {
        liker.cancelLikeIfExists(comment);
    }

    //== hate ==//
    @Transactional
    public void hateCommentForMember(final Long memberId, final Long commentId, final Boolean enable) {
        Member hater = findMember(memberId);
        Comment comment = findById(commentId);

        if (enable) {
            doHate(hater, comment);
            return;
        }
        cancelHate(hater, comment);
    }

    private void doHate(Member hater, Comment comment) {
        hater.hateCommentIfNew(comment);
    }

    private void cancelHate(Member hater, Comment comment) {
        hater.cancelHateIfExists(comment);
    }


    //== delete ==//
    @Transactional
    public void deleteComment(final Long memberId, final Long commentId) {

        Member member = findMember(memberId);
        Comment comment = findById(commentId);

        checkMemberCanTouchComment(member, comment);

        comment.remove();
        // 명시적 삭제
        commentRepository.delete(comment);
        // commentRepository.deleteIfMemberCanTouchComment(memberId, commentId);
    }

    @Transactional
    public CommentResponse modifyMembersCommentContent(final Long memberId, final Long commentId, final String content) {
        Member member = findMember(memberId);
        Comment comment = findById(commentId);

        validateContent(content);
        checkMemberCanTouchComment(member, comment);

        comment.changeContent(content);
        return CommentResponse.from(comment);
    }

    private void checkMemberCanTouchComment(Member member, Comment comment) {
        // member가 admin or 댓글 작성자
        if (!member.isAdmin() && !comment.isWrittenBy(member)) {
            throw new IllegalCommentStatusChangeException(member.getId(), comment.getId());
        }
    }

    public CommentResponse getLatestCommentOfTopic(Long topicId) {
        return CommentResponse.from(
                commentRepository
                .findFirstByTopicIdOrderByCreatedAtDesc(topicId)
                .orElse(null)
        );
    }
}
