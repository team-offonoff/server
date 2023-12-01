package life.offonoff.ab.domain.topic;

import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TopicTest {

    @Test
    @DisplayName("Topic 생성 후 Member와 Keyword에서 접근시 동일 객체")
    void Topic_생성_매핑_후_Member와Keyword_연관관계_테스트() {
        // given
        Topic topic = TestEntityUtil.createTopic(0, TopicSide.TOPIC_A);
        Keyword keyword = new Keyword("key", TopicSide.TOPIC_A);
        Member member = TestEntityUtil.createMember("email", "password");

        // when
        topic.associate(member, List.of(keyword), null);

        // then
        assertAll(
                () -> assertThat(topic.getTopicKeywords().get(0).getKeyword().getName()).isEqualTo("key"),
                () -> assertThat(topic.getAuthor().getAuthInfo().getEmail()).isEqualTo("email")
        );
    }
}