package life.offonoff.ab.application.service;

import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Provider;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.exception.TopicReportDuplicateException;
import life.offonoff.ab.repository.KeywordRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.TopicResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
public class TopicServiceIntegrationTest {

    @Autowired MemberService memberService;

    @Autowired TopicService topicService;

    @Autowired KeywordRepository keywordRepository;

    @Autowired TopicRepository topicRepository;

    @Test
    void createTopicWithNewKeyword_saveKeyword() {
        // given
        Member member = memberService.join(new SignUpRequest("email", "password", Provider.NONE));

        // when
        Long topicId = topicService.createMembersTopic(
                member.getId(),
                TopicTestDtoHelper.builder()
                        .topicSide(TopicSide.TOPIC_A)
                        .keyword(new Keyword("key", TopicSide.TOPIC_A))
                        .build().createRequest()).topicId();

        // then
        Optional<Keyword> keyword = keywordRepository.findByNameAndSide("key", TopicSide.TOPIC_A);
        assertThat(keyword).isNotEmpty();
        assertThat(keyword.get().getTopics().get(0).getId()).isEqualTo(topicId);
    }

    @Test
    void reportTopicByMember_createTopicReport() {
        // given
        Member member = memberService.join(new SignUpRequest("email", "password", Provider.NONE));
        TopicResponse response = topicService.createMembersTopic(
                member.getId(),
                TopicTestDtoHelper.builder()
                        .topicSide(TopicSide.TOPIC_A)
                        .keyword(new Keyword("key", TopicSide.TOPIC_A))
                        .build().createRequest());

        // when
        topicService.reportTopicByMember(response.topicId(), member.getId());

        // then
        Topic topic = topicRepository.findByIdAndActiveTrue(response.topicId()).get();
        assertThat(topic.isReportedBy(member)).isTrue();
        assertThat(topic.getReports().size()).isOne();
        assertThat(topic.getReports().get(0).getTopic().getId()).isEqualTo(topic.getId());
    }

    @Test
    void reportTopicByMember_reportTwice_doNotCreateReportAgain_throwException() {
        // given
        Member member = memberService.join(new SignUpRequest("email", "password", Provider.NONE));
        TopicResponse response = topicService.createMembersTopic(
                member.getId(),
                TopicTestDtoHelper.builder()
                        .topicSide(TopicSide.TOPIC_A)
                        .keyword(new Keyword("key", TopicSide.TOPIC_A))
                        .build().createRequest());

        // when
        topicService.reportTopicByMember(response.topicId(), member.getId());
        assertThatThrownBy(() -> topicService.reportTopicByMember(response.topicId(), member.getId()))
                .isInstanceOf(TopicReportDuplicateException.class);
        Topic topic = topicRepository.findByIdAndActiveTrue(response.topicId()).get();
        assertThat(topic.getReports().size()).isOne();
    }
}
