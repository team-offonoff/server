package life.offonoff.ab.application.service;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.request.TopicCreateRequest;
import life.offonoff.ab.application.service.vote.TestVoteConfig;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainer;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.TestKeyword;
import static life.offonoff.ab.domain.TestEntityUtil.TestMember;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@Import(TestVoteConfig.TestContainerVotingTopicConfig.class)
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
        Keyword keyword = TestKeyword.builder()
                .name("키워드") // 6자까지만 가능
                .build().buildKeyword();
        em.persist(keyword);

        // publish member 생성
        Member member = TestMember.builder()
                .build().buildMember();
        em.persist(member);

        TopicCreateRequest request = TopicServiceTest.TopicTestDtoHelper.builder()
                .keywords(List.of(keyword))
                .build()
                .createRequest();

        // when
        topicService.createMembersTopic(member.getId(), request);

        // then
        assertThat(container.size()).isEqualTo(1);
    }
}
