package life.offonoff.ab.domain.comment;

import life.offonoff.ab.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    @DisplayName("댓글 좋아요 후 댓글 좋아요 수 증가")
    void like_comment() {
        // given
        Comment comment = new Comment("content");
        Member liker = createMember("email", "password");

        // when
        new LikedComment(liker, comment);

        // then
        assertThat(comment.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 좋아요 누른 댓글은 변화 X")
    void like_comment_duplicate() {
        // given
        Comment comment = new Comment("content");
        Member liker = createMember("email", "password");

        // when
        new LikedComment(liker, comment);
        new LikedComment(liker, comment);

        // then
        assertThat(comment.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("댓글 싫어요 후 댓글 좋아요 수 증가")
    void hate_comment() {
        // given
        Comment comment = new Comment("content");
        Member hate = createMember("email", "password");

        // when
        new HatedComment(hate, comment);

        // then
        assertThat(comment.getHateCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 싫어요 누른 댓글은 변화 X")
    void hate_comment_duplicate() {
        // given
        Comment comment = new Comment("content");
        Member hate = createMember("email", "password");

        // when
        new HatedComment(hate, comment);
        new HatedComment(hate, comment);

        // then
        assertThat(comment.getHateCount()).isEqualTo(1);
    }

}