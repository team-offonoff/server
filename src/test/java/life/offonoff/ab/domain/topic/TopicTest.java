package life.offonoff.ab.domain.topic;

import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    @Test
    @DisplayName("Topic 생성 후 Member와 Keyword에서 접근시 동일 객체")
    void Topic_생성_매핑_후_Member와Keyword_연관관계_테스트() {
        // given
        int seq = 0;
        TopicSide side = TopicSide.TOPIC_A;

        Topic topic = TestEntityUtil.createTopic(seq, side);
        Keyword keyword = TestEntityUtil.createKeyword(seq);
        Member member = TestEntityUtil.createMember("email", "password");

        // when
        topic.associate(member, keyword, null);

        // then
        assertAll(
                () -> assertThat(topic.getKeyword()).isEqualTo(keyword),
                () -> assertThat(topic.getPublishMember()).isEqualTo(member)
        );
    }
}