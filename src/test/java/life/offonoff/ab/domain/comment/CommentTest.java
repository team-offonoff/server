package life.offonoff.ab.domain.comment;

import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.TopicSide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    @DisplayName("댓글 좋아요 후 댓글 좋아요 수 증가")
    void like_comment() {
        // given
        Comment comment = TestComment.builder()
                .id(1L)
                .writer(createMember("writer", "password"))
                .topic(createTopic(0, TopicSide.TOPIC_B))
                .build().buildComment();

        Member liker = createMember("email", "password");

        // when
        liker.likeCommentIfNew(comment);


        // then
        assertThat(comment.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 좋아요 누른 댓글은 변화 X")
    void like_comment_duplicate() {
        // given
        Comment comment = TestComment.builder()
                .id(1L)
                .writer(createMember("writer", "password"))
                .topic(createTopic(0, TopicSide.TOPIC_B))
                .build().buildComment();

        Member liker = createMember("email", "password");

        // when
        liker.likeCommentIfNew(comment);
        liker.likeCommentIfNew(comment);

        // then
        assertThat(comment.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 싫어요 후 댓글 좋아요 수 증가")
    void hate_comment() {
        // given
        Comment comment = TestComment.builder()
                .id(1L)
                .writer(createMember("writer", "password"))
                .topic(createTopic(0, TopicSide.TOPIC_B))
                .build().buildComment();

        Member hater = createMember("email", "password");

        // when
        hater.hateCommentIfNew(comment);

        // then
        assertThat(comment.getHateCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 싫어요 누른 댓글은 변화 X")
    void hate_comment_duplicate() {
        // given
        Comment comment = TestComment.builder()
                .id(1L)
                .writer(createMember("writer", "password"))
                .topic(createTopic(0, TopicSide.TOPIC_B))
                .build().buildComment();

        Member hater = createMember("email", "password");

        // when
        hater.hateCommentIfNew(comment);
        hater.hateCommentIfNew(comment);

        // then
        assertThat(comment.getHateCount()).isEqualTo(1);
    }

}