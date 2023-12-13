package life.offonoff.ab.application.service;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Role;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.web.response.CommentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;

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

    /*
    @Test
    @DisplayName("토픽 작성자에 의한 댓글 삭제")
    void delete_comment_by_topic_author() {
        // given
        Member topicAuthor = createCompletelyJoinedMember("author", "pwd", "author");
        Member writer = createCompletelyJoinedMember("writer", "pwd", "writer");
        Topic topic = TestTopicUtil.createTopicWithAuthor(TopicSide.TOPIC_A, topicAuthor);

        Comment comment = new Comment(writer, topic, "content");

        em.persist(writer);
        em.persist(topic);
        em.persist(comment);

        // when
        commentService.deleteComment(topicAuthor.getId(), comment.getId());

        // then
        assertThat(topic.getCommentCount()).isEqualTo(0);
    }
     */
}