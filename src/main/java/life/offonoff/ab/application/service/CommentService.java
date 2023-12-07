package life.offonoff.ab.application.service;

import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.exception.MemberByIdNotFoundException;
import life.offonoff.ab.exception.TopicNotFoundException;
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

    @Transactional
    public CommentResponse register(Long memberId, CommentRequest request) {

        Member member = findMember(memberId);
        Topic topic = findTopic(request.getTopicId());

        Comment comment = new Comment(member, topic, request.getContent());
        commentRepository.save(comment);

        return CommentResponse.from(comment);
    }

    public Slice<CommentResponse> findAll(Long topicId, Pageable pageable) {
        return commentRepository.findAll(topicId, pageable)
                                .map(CommentResponse::from);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberByIdNotFoundException(memberId));
    }

    private Topic findTopic(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
    }
}
