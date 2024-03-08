package life.offonoff.ab.application.service.vote;

import life.offonoff.ab.application.service.vote.criteria.VoteClosingCriteria;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainer;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.notfication.NotificationRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static life.offonoff.ab.domain.TestEntityUtil.TestMember;
import static life.offonoff.ab.domain.TestEntityUtil.TestTopic;
import static life.offonoff.ab.domain.topic.TopicStatus.CLOSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
@Import(TestVoteConfig.TestContainerVotingTopicConfig.class)
public class VotingTopicContainerServiceIntegrationTest {

    @Autowired
    VotingTopicService votingTopicContainerService;

    @MockBean
    VoteClosingCriteria criteria;
    @MockBean
    VotingTopicContainer container;
    @MockBean
    TopicRepository topicRepository;
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    NotificationRepository notificationRepository;

    @Test
    @DisplayName("투표가 끝난 토픽은 status 수정 & Voting Result 매핑")
    void endVote_then_status_voting_result() throws InterruptedException {
        // given
        LocalDateTime deadline = LocalDateTime.now();
        Topic topic = TestTopic.builder()
                .id(1L)
                .deadline(deadline)
                .build().buildTopic();

        VotingTopic votingTopic = new VotingTopic(topic);
        List<VotingTopic> votingTopics = List.of(votingTopic);

        when(container.getVotingEnded(criteria)).thenReturn(votingTopics);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executorService.execute(() -> {
            votingTopicContainerService.endVote(criteria);
            latch.countDown();
        });

        executorService.execute(() -> {
            votingTopicContainerService.endVote(criteria);
            latch.countDown();
        });
        latch.await();

        // then
        assertThat(topic.getStatus()).isEqualTo(CLOSED);
    }

    @Test
    @DisplayName("투표가 끝나면, 투표 결과 공지")
    void notice() {
        // given
        Topic topic = TestTopic.builder()
                .id(1L)
                .deadline(LocalDateTime.now())
                .build().buildTopic();
        // Vote Member
        Member member = TestMember.builder()
                .id(1L)
                .build().buildMember();
        List<Member> voteMembers = List.of(member);

        // Voting Topic
        VotingTopic votingTopic = new VotingTopic(topic);
        List<VotingTopic> votingTopics = List.of(votingTopic);

        when(container.getVotingEnded(criteria)).thenReturn(votingTopics);
        when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));
        when(memberRepository.findAllListeningVoteResultAndVotedTopicId(topic.getId())).thenReturn(voteMembers);
        doNothing().when(notificationRepository).saveAll(any());
        
        // when
        votingTopicContainerService.endVote(criteria);

        // then
        assertAll(
                () -> assertThat(member.getNotifications().size()).isGreaterThan(0)
        );
    }
}