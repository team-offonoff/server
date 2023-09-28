package life.offonoff.ab.domain.comment;

import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    @Test
    @DisplayName("댓글 작성 시 토픽의 댓글 수 증가")
    void comment_count_increase() {
        // given
        int seq = 0;
        Member member = TestEntityUtil.createMember(seq);
        Topic topic = TestEntityUtil.createTopic(seq, TopicSide.TOPIC_A);

        // when
        Comment comment = TestEntityUtil.createComment(seq);
        comment.associate(member, topic);

        // then
        assertAll(
                () -> assertThat(topic.getCommentCount()).isEqualTo(1),
                () -> assertThat(member.getComments()).contains(comment)
        );
    }
}