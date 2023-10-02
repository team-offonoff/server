package life.offonoff.ab.domain.topic.hide;

import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class HiddenTopicTest {

    @Test
    @DisplayName("토픽 블락 후 토픽의 blockCount 1 증가")
    void block_count_increase() {
        // given
        int seq = 0;
        Topic topic = createTopic(seq, TopicSide.TOPIC_A);
        Member member = createMember(seq);

        // when
        HiddenTopic block = new HiddenTopic();
        block.associate(member, topic);

        // then
        assertAll(
                () -> assertThat(topic.getHideCount()).isEqualTo(1),
                () -> assertThat(member.getHiddenTopics()).contains(block)
        );
    }

    @Test
    @DisplayName("토픽 hide 후 hide 여부 테스트")
    void 중복_hide테스트() {
        // given
        int seq = 0;
        Member member = createMember(seq);
        Topic topic = createTopic(seq, TopicSide.TOPIC_A);

        // when
        HiddenTopic hiddenTopic = new HiddenTopic();
        hiddenTopic.associate(member, topic);

        // then
        assertAll(
                () -> assertThat(hiddenTopic.has(member)).isTrue()
        );

    }
}