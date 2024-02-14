package life.offonoff.ab.repository.member;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.configuration.TestJPAConfig;
import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.TestEntityUtil.TestMember;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.NotificationEnabled;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestJPAConfig.class)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("닉네임 중복 등록시에는 예외")
    void nickname_exists() {
        // given
        String nickname = "nickname";

        Member member = TestMember.builder()
                .email("email")
                .nickname(nickname)
                .build().buildMember();

        memberRepository.save(member);

        // when
        boolean nicknameExists = memberRepository.existsByNickname(nickname);

        // then
        assertThat(nicknameExists).isTrue();
    }

    @Test
    @DisplayName("VoteResult 알림 수신 미동의한 Member는 조회 X")
    void find_VoteResultListening_voters() {
        // given
        Member voter = TestMember.builder()
                .build().buildMember();
        // Disable VoteResult Notice
        voter.changeNotificationEnabled(
                new NotificationEnabled(false, true, true, true));

        memberRepository.save(voter);

        // Topic
        Topic topic = TestTopic.builder()
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();
        em.persist(topic);

        // Choice
        Choice choice = createChoice(topic, ChoiceOption.CHOICE_A, null);
        em.persist(choice);

        // Vote
        Vote vote = createVote(choice.getChoiceOption());
        vote.associate(voter, topic);
        em.persist(vote);

        // when
        List<Member> voters = memberRepository.findAllListeningVoteResultAndVotedTopicId(topic.getId());

        // then
        assertThat(voters).doesNotContain(voter);
    }

    @Test
    @DisplayName("VoteResult 알림 수신 미동의한 Author는 조회 X")
    void find_VoteResultListening_Author() {
        // given
        Member author = TestMember.builder()
                .build().buildMember();
        // Disable VoteResult Notice
        author.changeNotificationEnabled(
                new NotificationEnabled(false, true, true, true));

        memberRepository.save(author);

        // Topic
        Topic topic = TestTopic.builder()
                .side(TopicSide.TOPIC_B)
                .author(author)
                .build().buildTopic();
        em.persist(topic);

        // Choice
        Choice choice = createChoice(topic, ChoiceOption.CHOICE_A, null);
        em.persist(choice);

        // Vote
        Vote vote = createVote(choice.getChoiceOption());
        vote.associate(author, topic);
        em.persist(vote);

        // when
        List<Member> voters = memberRepository.findAllListeningVoteResultAndVotedTopicId(topic.getId());

        // then
        assertThat(voters).doesNotContain(author);
    }
}