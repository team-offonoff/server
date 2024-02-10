package life.offonoff.ab.application.service;

import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.VoteRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.*;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.AlreadyVotedException;
import life.offonoff.ab.exception.FutureTimeRequestException;
import life.offonoff.ab.exception.TopicReportDuplicateException;
import life.offonoff.ab.exception.VoteByAuthorException;
import life.offonoff.ab.repository.VoteRepository;
import life.offonoff.ab.repository.keyword.KeywordRepository;
import life.offonoff.ab.repository.member.MemberRepository;
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
    @Autowired MemberRepository memberRepository;

    @Test
    void createTopicWithNewKeyword_saveKeyword() {
        // given
        Member member = createMemberByEmailAndNickname("email", "nickname");

        // when
        Long topicId = topicService.createMembersTopic(
                member.getId(),
                TopicTestDtoHelper.builder()
                        .topicSide(TopicSide.TOPIC_B)
                        .keyword(new Keyword("key", TopicSide.TOPIC_B))
                        .build().createRequest()).getTopicId();

        // then
        Optional<Keyword> keyword = keywordRepository.findByNameAndSide("key", TopicSide.TOPIC_B);
        assertThat(keyword).isNotEmpty();
        assertThat(keyword.get().getTopics().get(0).getId()).isEqualTo(topicId);
    }

    @Test
    void createMembersTopic_withoutKeywordAndDeadlineAsSideA() {
        // given
        Member member = createMemberByEmailAndNickname("email", "nickname");

        // when
        Long topicId = topicService.createMembersTopic(
                member.getId(),
                TopicTestDtoHelper.builder()
                        .topicSide(TopicSide.TOPIC_A)
                        .deadline(null)
                        .build().createRequest()).getTopicId();

        // then
        Topic topic = topicRepository.findById(topicId).get();
        assertThat(topic.getKeyword()).isNull();
        assertThat(topic.getDeadline()).isNull();
    }

    @Test
    void reportTopicByMember_createTopicReport() {
        // given
        Member member = createMemberByEmailAndNickname("email", "nickname");

        TopicResponse response = createMembersTopic(member.getId());

        // when
        topicService.reportTopicByMember(response.getTopicId(), member.getId());

        // then
        Topic topic = topicRepository.findByIdAndActiveTrue(response.getTopicId()).get();
        assertThat(topic.isReportedBy(member)).isTrue();
        assertThat(topic.getReports().size()).isOne();
        assertThat(topic.getReports().get(0).getTopic().getId()).isEqualTo(topic.getId());
    }

    @Test
    void reportTopicByMember_reportTwice_doNotCreateReportAgain_throwException() {
        // given
        Member member = createMemberByEmailAndNickname("email", "nickname");

        TopicResponse response = createMembersTopic(member.getId());

        // when
        topicService.reportTopicByMember(response.getTopicId(), member.getId());
        assertThatThrownBy(() -> topicService.reportTopicByMember(response.getTopicId(), member.getId()))
                .isInstanceOf(TopicReportDuplicateException.class);
        Topic topic = topicRepository.findByIdAndActiveTrue(response.getTopicId()).get();
        assertThat(topic.getReports().size()).isOne();
    }

    @Test
    void activateMembersTopic_deactivateTopic() {
        // given
        Member member = createMemberByEmailAndNickname("email", "nickname");
        TopicResponse response = createMembersTopic(member.getId());

        // when
        topicService.activateMembersTopic(member.getId(), response.getTopicId(), false);

        // then
        assertThat(topicRepository.findByIdAndActiveTrue(response.getTopicId())).isEmpty();
    }

    @Test
    void deleteMembersTopic_doesntAffectMember() {
        // given
        Member member = createMemberByEmailAndNickname("email", "nickname");
        TopicResponse response = createMembersTopic(member.getId());

        // when
        topicService.deleteMembersTopic(member.getId(), response.getTopicId());

        // then
        assertThat(memberRepository.findByIdAndActiveTrue(member.getId())).isNotEmpty();
        assertThat(topicRepository.findAll()).isEmpty();
    }

    @Test
    void deleteMembersTopic_doesntAffectKeyword() {
        // given
        Member member = createMemberByEmailAndNickname("email", "nickname");
        TopicResponse response = createMembersTopic(member.getId());

        // when
        Long keywordId = response.getKeyword().keywordId();
        topicService.deleteMembersTopic(member.getId(), response.getTopicId());

        // then
        assertThat(keywordRepository.findById(keywordId)).isNotEmpty();
        assertThat(topicRepository.findAll()).isEmpty();
    }


    @Test
    void voteForTopicByMember_byNonAuthor_success() {
        Member author = createMemberByEmailAndNickname("author_email", "author");
        Member voter = createMemberByEmailAndNickname("voter_email", "voter");
        TopicResponse response = createMembersTopic(author.getId());

        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        ThrowingCallable code = () ->
                topicService.voteForTopicByMember(response.getTopicId(), voter.getId(), request);

        assertThatNoException().isThrownBy(code);
        List<Vote> votes = topicRepository.findById(response.getTopicId()).get().getVotes();
        assertThat(votes).isNotEmpty();
        assertThat(voteRepository.findAll()).isNotEmpty();
    }

    @Test
    void voteForTopicByMember_byAuthor_throwException() {
        Member author = createMemberByEmailAndNickname("email", "nickname");
        TopicResponse response = createMembersTopic(author.getId());

        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        ThrowingCallable code = () ->
                topicService.voteForTopicByMember(response.getTopicId(), author.getId(), request);

        assertThatThrownBy(code)
                .isInstanceOf(VoteByAuthorException.class);
    }

    @Test
    void voteForTopicByMember_votedAtFuture_throwException() {
        Member author = createMemberByEmailAndNickname("author_email", "author");
        Member voter = createMemberByEmailAndNickname("voter_email", "voter");
        TopicResponse response = createMembersTopic(author.getId());

        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toEpochSecond());
        ThrowingCallable code = () ->
                topicService.voteForTopicByMember(response.getTopicId(), voter.getId(), request);

        assertThatThrownBy(code)
                .isInstanceOf(FutureTimeRequestException.class);
    }

    @Test
    void voteForTopicByMember_duplicateVote_throwException() {
        Member author = createMemberByEmailAndNickname("author_email", "author");
        Member voter = createMemberByEmailAndNickname("voter_email", "voter");
        TopicResponse response = createMembersTopic(author.getId());
        VoteRequest request = new VoteRequest(
                ChoiceOption.CHOICE_A, LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        topicService.voteForTopicByMember(response.getTopicId(), voter.getId(), request);

        ThrowingCallable code = () ->
                topicService.voteForTopicByMember(response.getTopicId(), voter.getId(), request);

        assertThatThrownBy(code)
                .isInstanceOf(AlreadyVotedException.class);
    }

    private Member createMemberByEmailAndNickname(String email, String nickname) {
        Member member = memberService.join(new SignUpRequest(email, "password", Provider.NONE));
        member.registerPersonalInfo(new PersonalInfo(nickname, LocalDate.now(), Gender.MALE, "job"));
        member.agreeTerms(new TermsEnabled(true));

        return member;
    }

    private TopicResponse createMembersTopic(final Long memberId) {
        return topicService.createMembersTopic(
                memberId,
                TopicTestDtoHelper.builder()
                        .topicSide(TopicSide.TOPIC_B)
                        .keyword(new Keyword("key", TopicSide.TOPIC_B))
                        .build().createRequest());
    }
}
