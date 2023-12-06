package life.offonoff.ab.application.service;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.web.response.CommentResponse;
import org.assertj.core.api.Assertions;
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
}