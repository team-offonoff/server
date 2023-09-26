package life.offonoff.ab.domain.topic;

import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    @Test
    @DisplayName("Topic 생성 후 Member와 Category에서 접근시 동일 객체")
    void Topic_생성_매핑_후_Member와Category_연관관계_테스트() {
        // given
        int seq = 0;
        TopicSide side = TopicSide.TOPIC_A;

        Topic topic = TestEntityUtil.createTextTopic(seq, side);
        Category category = TestEntityUtil.createCategory(seq);
        Member member = TestEntityUtil.createMember(seq);

        // when
        topic.associate(member, category);

        // then
        assertAll(
                () -> assertThat(topic.getCategory()).isEqualTo(category),
                () -> assertThat(topic.getPublishMember()).isEqualTo(member)
        );
    }
}