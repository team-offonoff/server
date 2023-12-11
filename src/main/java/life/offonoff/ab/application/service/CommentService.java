package life.offonoff.ab.application.service;

import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.comment.HatedComment;
import life.offonoff.ab.domain.comment.LikedComment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.comment.CommentRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TopicRepository topicRepository;

    //== find ==//
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    public Slice<CommentResponse> findAll(Long topicId, Pageable pageable) {
        return commentRepository.findAll(topicId, pageable)
                .map(CommentResponse::from);
    }

    private Member findMember(final Long memberId) {
        return memberRepository.findByIdAndActiveTrue(memberId)
                .orElseThrow(() -> new MemberByIdNotFoundException(memberId));
    }

    private Topic findTopic(Long topicId) {
        return topicRepository.findByIdAndActiveTrue(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
    }

    //== save ==//
    @Transactional
    public CommentResponse register(Long memberId, CommentRequest request) {

        Member member = findMember(memberId);
        Topic topic = findTopic(request.getTopicId());

        Comment comment = new Comment(member, topic, request.getContent());
        commentRepository.save(comment);

        return CommentResponse.from(comment);
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
        new LikedComment(liker, comment);
    }

    private void cancelLike(Member liker, Comment comment) {
        liker.cancelLike(comment);
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
        new HatedComment(hater, comment);
    }

    private void cancelHate(Member hater, Comment comment) {
        hater.cancelHate(comment);
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

    private void checkMemberCanTouchComment(Member member, Comment comment) {
        // member가 admin or 댓글 작성자 or 토픽 작성자
        if (!member.isAdmin() &&
            !member.isAuthorOf(comment.getTopic()) &&
            !comment.isWrittenBy(member)
        ) {
            throw new IllegalCommentStatusChangeException(member.getId(), comment.getId());
        }
    }
}
