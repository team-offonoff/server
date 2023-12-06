package life.offonoff.ab.application.service;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.VoteCancelRequest;
import life.offonoff.ab.application.service.request.VoteRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.*;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.FutureTimeRequestException;
import life.offonoff.ab.exception.TopicReportDuplicateException;
import life.offonoff.ab.exception.VoteByAuthorException;
import life.offonoff.ab.exception.MemberNotVoteException;
import life.offonoff.ab.repository.KeywordRepository;
import life.offonoff.ab.repository.VoteRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.topic.TopicResponse;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static life.offonoff.ab.application.service.TopicServiceTest.TopicTestDtoHelper;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
public class TopicServiceIntegrationTest {

    @Autowired MemberService memberService;

    @Autowired TopicService topicService;

    @Autowired KeywordRepository keywordRepository;

    @Autowired TopicRepository topicRepository;
    @Autowired VoteRepository voteRepository;

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

    @Test
    void voteForTopicByMember_byNonAuthor_success() {
        Member author = createMember();
        Member voter = createMember();
        TopicResponse response = createMembersTopic(author.getId());

        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        ThrowingCallable code = () ->
                topicService.voteForTopicByMember(response.topicId(), voter.getId(), request);

        assertThatNoException().isThrownBy(code);
        List<Vote> votes = topicRepository.findById(response.topicId()).get().getVotes();
        assertThat(votes).isNotEmpty();
        assertThat(voteRepository.findAll()).isNotEmpty();
    }

    @Test
    void voteForTopicByMember_byAuthor_throwException() {
        Member author = createMember();
        TopicResponse response = createMembersTopic(author.getId());

        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        ThrowingCallable code = () ->
                topicService.voteForTopicByMember(response.topicId(), author.getId(), request);

        assertThatThrownBy(code)
                .isInstanceOf(VoteByAuthorException.class);
    }

    @Test
    void voteForTopicByMember_votedAtFuture_throwException() {
        Member author = createMember();
        Member voter = createMember();
        TopicResponse response = createMembersTopic(author.getId());

        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toEpochSecond());
        ThrowingCallable code = () ->
                topicService.voteForTopicByMember(response.topicId(), voter.getId(), request);

        assertThatThrownBy(code)
                .isInstanceOf(FutureTimeRequestException.class);
    }

    @Test
    void cancelVoteForTopicByMember_existingVote_success() {
        Member author = createMember();
        Member voter = createMember();
        TopicResponse response = createMembersTopic(author.getId());
        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        topicService.voteForTopicByMember(response.topicId(), voter.getId(), request);

        topicService.cancelVoteForTopicByMember(
                response.topicId(), voter.getId(),
                new VoteCancelRequest(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()));
        // Topic is still alive
        Topic topic = topicRepository.findById(response.topicId()).get();
        assertThat(topic.getVotes()).isEmpty();
        // No votes left
        assertThat(voteRepository.findAll()).isEmpty();
    }

    @Test
    void cancelVoteForTopicByMember_nonExistingVote_throwException() {
        Member author = createMember();
        Member voter = createMember();
        TopicResponse response = createMembersTopic(author.getId());

        ThrowingCallable code = () -> topicService.cancelVoteForTopicByMember(
                response.topicId(), voter.getId(),
                new VoteCancelRequest(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()));

        assertThatThrownBy(code)
                .isInstanceOf(MemberNotVoteException.class);
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
