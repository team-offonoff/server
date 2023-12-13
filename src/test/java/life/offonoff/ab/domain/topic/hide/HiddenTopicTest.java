package life.offonoff.ab.domain.topic.hide;

import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Provider;
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
        Topic topic = TestTopic.builder()
                .id(1L)
                .author(createMember("author", "author"))
                .build().buildTopic();

        Member member = new Member("email", "password", Provider.NONE);

        // when
        member.hideTopicIfNew(topic);

        // then
        assertThat(topic.getHideCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("토픽 hide 후 hide 여부 테스트")
    void 중복_hide테스트() {
        // given
        Topic topic = TestTopic.builder()
                .id(1L)
                .author(createMember("author", "author"))
                .build().buildTopic();

        Member member = new Member("email", "password", Provider.NONE);

        // when
        member.hideTopicIfNew(topic);

        // then
        assertAll(
                () -> assertThat(member.hideAlready(topic)).isTrue()
        );

    }
}