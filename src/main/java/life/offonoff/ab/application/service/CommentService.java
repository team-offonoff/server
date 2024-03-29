package life.offonoff.ab.application.service;

import life.offonoff.ab.application.event.report.CommentReportEvent;
import life.offonoff.ab.application.event.topic.CommentLikedEvent;
import life.offonoff.ab.application.event.topic.CommentedEvent;
import life.offonoff.ab.application.service.common.TextUtils;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.comment.LikedComment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.VoteRepository;
import life.offonoff.ab.repository.comment.CommentRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.common.response.PageResponse;
import life.offonoff.ab.web.response.CommentReactionResponse;
import life.offonoff.ab.web.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static life.offonoff.ab.application.service.common.LengthInfo.COMMENT_CONTENT;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TopicRepository topicRepository;
    private final VoteRepository voteRepository;

    private final ApplicationEventPublisher eventPublisher;

    //== find ==//
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    public PageResponse<CommentResponse> findAll(Long memberId, Long topicId, Pageable pageable) {

        checkMemberCanViewComments(memberId, topicId);

        Member member = findMemberWithLikedComments(memberId);

        final int commentCount = topicRepository.findCommentCountById(topicId);

        final Slice<CommentResponse> commentResponseSlice =
                commentRepository.findAll(topicId, pageable)
                                 .map(comment -> CommentResponse.from(comment, member));

        return PageResponse.of(commentCount, commentResponseSlice);
    }

    private void checkMemberCanViewComments(Long memberId, Long topicId) {
        Topic topic = findTopic(topicId);
        Member member = findMember(memberId);

        if (!topic.isWrittenBy(member) &&
                !voteRepository.existsByVoterIdAndTopicId(memberId, topicId)) {
            throw new UnableToViewCommentsException(topicId);
        }
    }

    private Member findMember(final Long memberId) {
        return memberRepository.findByIdAndActiveTrue(memberId)
                .orElseThrow(() -> new MemberByIdNotFoundException(memberId));
    }

    private Member findMemberWithLikedComments(final Long memberId) {
        return memberRepository.findByIdWithLikedComments(memberId)
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
        Comment comment = createComment(memberId, request);

        try {
            // 토픽의 댓글수 먼저 업데이트
            topicRepository.flush();
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn(e.getMessage());
            throw new CommentConcurrencyException();
        }

        commentRepository.save(comment);

        eventPublisher.publishEvent(new CommentedEvent(comment));

        return CommentResponse.from(comment);
    }

    private Comment createComment(Long memberId, CommentRequest request) {
        final String content = request.getContent();

        validateContent(content);

        Member writer = findMember(memberId);
        Topic topic = findTopic(request.getTopicId());

        if (topic.isWrittenBy(writer)) {
            return Comment.createAuthorsComment(writer, topic, content);
        }
        return Comment.createVotersComment(findVote(writer.getId(), topic.getId()), content);
    }

    private void validateContent(String content) {
        final int length = TextUtils.countGraphemeClusters(content);
        if (length > COMMENT_CONTENT.getMaxLength() || length < COMMENT_CONTENT.getMinLength()) {
            throw new LengthInvalidException("댓글 내용", COMMENT_CONTENT);
        }
    }

    //== like ==//
    @Transactional
    public CommentReactionResponse likeCommentByMember(final Long likerId, final Long commentId, final Boolean enable) {
        Member liker = findMember(likerId);
        Comment comment = findById(commentId);

        if (enable) {
            return doLike(liker, comment);
        }
        return cancelLike(liker, comment);
    }

    private CommentReactionResponse doLike(Member liker, Comment comment) {
        liker.cancelHateIfExists(comment);
        liker.likeCommentIfNew(comment)
                .ifPresent(this::afterLike);
        return new CommentReactionResponse(
                comment.getLikeCount(), comment.getHateCount(), true, false);
    }

    private void afterLike(LikedComment likedComment) {
        eventPublisher.publishEvent(new CommentLikedEvent(likedComment));
    }

    private CommentReactionResponse cancelLike(Member liker, Comment comment) {

        liker.cancelLikeIfExists(comment);
        return new CommentReactionResponse(comment.getLikeCount(), comment.getHateCount(), false, false);
    }

    //== hate ==//
    @Transactional
    public CommentReactionResponse hateCommentForMember(final Long memberId, final Long commentId, final Boolean enable) {
        Member hater = findMember(memberId);
        Comment comment = findById(commentId);

        if (enable) {
            return doHate(hater, comment);
        }
        return cancelHate(hater, comment);
    }

    private CommentReactionResponse doHate(Member hater, Comment comment) {
        hater.cancelLikeIfExists(comment);
        hater.hateCommentIfNew(comment);
        return new CommentReactionResponse(comment.getLikeCount(), comment.getHateCount(), false, true);
    }

    private CommentReactionResponse cancelHate(Member hater, Comment comment) {
        hater.cancelHateIfExists(comment);
        return new CommentReactionResponse(comment.getLikeCount(), comment.getHateCount(), false, false);
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

    @Transactional
    public void reportCommentByMember(final Long commentId, final Long memberId) {
        Member member = findMember(memberId);
        Comment comment = findById(commentId);

        if (comment.isReportedBy(member)) {
            throw new CommentReportDuplicateException(commentId, memberId);
        }
        comment.getReportedBy(member);

        eventPublisher.publishEvent(
                new CommentReportEvent(CommentResponse.from(comment), comment.getReports().size())
        );
    }
}
