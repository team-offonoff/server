package life.offonoff.ab.application.service;

import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.*;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.exception.TopicReportDuplicateException;
import life.offonoff.ab.repository.KeywordRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.topic.TopicResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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
        Member member = createMember();

        // when
        Long topicId = topicService.createMembersTopic(
                member.getId(),
                TopicTestDtoHelper.builder()
                        .topicSide(TopicSide.TOPIC_A)
                        .keywords(List.of(new Keyword("key", TopicSide.TOPIC_A)))
                        .build().createRequest()).topicId();

        // then
        Optional<Keyword> keyword = keywordRepository.findByNameAndSide("key", TopicSide.TOPIC_A);
        assertThat(keyword).isNotEmpty();
        assertThat(keyword.get().getTopicKeywords().get(0).getTopic().getId()).isEqualTo(topicId);
    }

    @Test
    void reportTopicByMember_createTopicReport() {
        // given
        Member member = createMember();

        TopicResponse response = createMembersTopic(member.getId());

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
        Member member = createMember();

        TopicResponse response = createMembersTopic(member.getId());

        // when
        topicService.reportTopicByMember(response.topicId(), member.getId());
        assertThatThrownBy(() -> topicService.reportTopicByMember(response.topicId(), member.getId()))
                .isInstanceOf(TopicReportDuplicateException.class);
        Topic topic = topicRepository.findByIdAndActiveTrue(response.topicId()).get();
        assertThat(topic.getReports().size()).isOne();
    }

    @Test
    void activateMembersTopic_deactivateTopic() {
        // given
        Member member = createMember();

        TopicResponse response = createMembersTopic(member.getId());

        // when
        topicService.activateMembersTopic(member.getId(), response.topicId(), false);

        // then
        assertThat(topicRepository.findByIdAndActiveTrue(response.topicId())).isEmpty();
    }

    private Member createMember() {
        Member member = memberService.join(new SignUpRequest("email", "password", Provider.NONE));
        member.registerPersonalInfo(new PersonalInfo("nickname", LocalDate.now(), Gender.MALE, "job"));
        member.agreeTerms(new TermsEnabled(true));

        return member;
    }

    private TopicResponse createMembersTopic(final Long memberId) {
        return topicService.createMembersTopic(
                memberId,
                TopicTestDtoHelper.builder()
                        .build().createRequest());
    }
}
