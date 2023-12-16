package life.offonoff.ab.application.service;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.common.LengthInfo;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Role;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.exception.CommentNotFoundException;
import life.offonoff.ab.exception.IllegalCommentStatusChangeException;
import life.offonoff.ab.exception.LengthInvalidException;
import life.offonoff.ab.web.response.CommentResponse;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class CommentServiceTest {

    @Autowired EntityManager em;

    @Autowired CommentService commentService;

    @Test
    @DisplayName("댓글 저장")
    void save_comment() {
        // given
        Member member = createCompletelyJoinedMember("email", "password", "nickname");
        Topic topic = createTopic(0, TopicSide.TOPIC_A);

        em.persist(member);
        em.persist(topic);

        CommentRequest request = new CommentRequest(topic.getId(), "content");

        // when
        CommentResponse response = commentService.register(member.getId(), request);

        // then
        assertThat(response.getTopicId()).isEqualTo(topic.getId());
    }

    @Test
    void register_withLongContent_throwsException() {
        Long memberId = createRandomMember();
        Long topicId = createRandomTopic();

        ThrowingCallable code = () -> commentService.register(
                memberId,
                new CommentRequest(
                        topicId,
                        "c".repeat(LengthInfo.COMMENT_CONTENT.getMaxLength() + 1))
        );

        assertThatThrownBy(code)
                .isInstanceOf(LengthInvalidException.class);
    }

    @Test
    @DisplayName("댓글 저장후 토픽 댓글 수 증가")
    void save_comment_topic_comment_count() {
        // given
        Member member = createCompletelyJoinedMember("email", "password", "nickname");
        Topic topic = createTopic(0, TopicSide.TOPIC_A);

        em.persist(member);
        em.persist(topic);

        int beforeCount = topic.getCommentCount();
        CommentRequest request = new CommentRequest(topic.getId(), "content");

        // when
        CommentResponse response = commentService.register(member.getId(), request);

        // then
        assertThat(topic.getCommentCount()).isEqualTo(beforeCount + 1);
    }

    @Test
    @DisplayName("ADMIN에 의한 댓글 삭제")
    void delete_comment_by_admin() {
        // given
        Member adminMember = TestMember.builder()
                .role(Role.ADMIN)
                .build().buildMember();
        System.out.println(adminMember.isAdmin());
        Member writer = createCompletelyJoinedMember("writer", "pwd", "writer");
        Topic topic = createTopic(0, TopicSide.TOPIC_A);

        Comment comment = new Comment(writer, topic, "content");

        em.persist(adminMember);
        em.persist(writer);
        em.persist(topic);
        em.persist(comment);

        // when
        commentService.deleteComment(adminMember.getId(), comment.getId());

        // then
        assertThat(topic.getCommentCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 작성자에 의한 댓글 삭제")
    void delete_comment_by_comment_writer() {
        // given
        Member writer = createCompletelyJoinedMember("writer", "pwd", "writer");
        Topic topic = createTopic(0, TopicSide.TOPIC_A);

        Comment comment = new Comment(writer, topic, "content");

        em.persist(writer);
        em.persist(topic);
        em.persist(comment);

        // when
        commentService.deleteComment(writer.getId(), comment.getId());

        // then
        assertThat(topic.getCommentCount()).isEqualTo(0);
    }

    @Test
    void modifyComment() {
        Long memberId = createRandomMember();
        Long topicId = createRandomTopic();
        Long commentId = commentService.register(
                memberId,
                new CommentRequest(topicId, "content")
        ).getCommentId();

        commentService.modifyMembersCommentContent(
                memberId, commentId,
                "new content");

        Comment comment = em.find(Comment.class, commentId);
        assertThat(comment.getContent()).isEqualTo("new content");
    }

    @Test
    void modifyComment_withInvalidCommentId_throwsException() {
        Long memberId = createRandomMember();
        Long topicId = createRandomTopic();
        Long commentId = commentService.register(
                memberId,
                new CommentRequest(topicId, "content")
        ).getCommentId();

        ThrowingCallable code = () -> commentService.modifyMembersCommentContent(
                memberId,
                commentId + 1,
                "new content");

        assertThatThrownBy(code)
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void modifyComment_withLongContent_throwsException() {
        Long memberId = createRandomMember();
        Long topicId = createRandomTopic();
        Long commentId = commentService.register(
                memberId,
                new CommentRequest(topicId, "content")
        ).getCommentId();

        ThrowingCallable code = () -> commentService.modifyMembersCommentContent(
                memberId, commentId,
                "c".repeat(LengthInfo.COMMENT_CONTENT.getMaxLength() + 1));

        assertThatThrownBy(code)
                .isInstanceOf(LengthInvalidException.class);
    }

    @Test
    void modifyComment_withNonAuthor_throwsException() {
        Long memberId = createRandomMember();
        Long topicId = createRandomTopic();
        Long commentId = commentService.register(
                memberId,
                new CommentRequest(topicId, "content")
        ).getCommentId();
        Long otherMemberId = createRandomMember();

        ThrowingCallable code = () -> commentService.modifyMembersCommentContent(
                otherMemberId,
                commentId, "new content");

        assertThatThrownBy(code)
                .isInstanceOf(IllegalCommentStatusChangeException.class);
    }

    private Long createRandomMember() {
        Member member =  createCompletelyJoinedMember("email", "password", "nickname");
        em.persist(member);
        return member.getId();
    }

    private Long createRandomTopic() {
        Topic topic = createTopic(0, TopicSide.TOPIC_A);
        em.persist(topic);
        return topic.getId();
    }
}