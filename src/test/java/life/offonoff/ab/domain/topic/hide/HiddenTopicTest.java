package life.offonoff.ab.domain.topic.hide;

import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class HiddenTopicTest {

    @Test
    @DisplayName("토픽 블락 후 토픽의 blockCount 1 증가")
    void block_count_increase() {
        // given
        int seq = 0;
        Topic topic = TestEntityUtil.createTextTopic(seq, TopicSide.TOPIC_A);
        Member member = TestEntityUtil.createMember(seq);

        // when
        HiddenTopic block = new HiddenTopic();
        block.associate(member, topic);

        // then
        assertAll(
                () -> assertThat(topic.getBlockCount()).isEqualTo(1),
                () -> assertThat(member.getHiddenBlocks()).contains(block)
        );
    }
}