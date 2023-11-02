package life.offonoff.ab.application.service;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.vote.votingtopic.VotingTopicContainer;
import life.offonoff.ab.application.service.request.TopicCreateRequest;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
public class VotingTopicContainerIntegrationTest {

    @Autowired
    EntityManager em;

    @Autowired
    TopicService topicService;

    @Autowired
    VotingTopicContainer container;

    @Test
    @DisplayName("토픽이 생성되면 VotingTopic 추가")
    void votingTopic_added_when_topic_created() {
        // given
        // 카테고리 생성
        Category category = TestCategory.builder()
                .name("category")
                .build().buildCategory();
        em.persist(category);

        // publish member 생성
        Member member = TestMember.builder()
                .name("member")
                .build().buildMember();
        em.persist(member);

        TopicCreateRequest request = TopicServiceTest.TopicTestDtoHelper.builder()
                .category(category)
                .build()
                .createRequest();

        // when
        topicService.createMembersTopic(member.getId(), request);

        // then
        assertThat(container.size()).isEqualTo(1);
    }
}
