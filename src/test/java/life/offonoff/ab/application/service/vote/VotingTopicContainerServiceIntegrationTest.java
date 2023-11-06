package life.offonoff.ab.application.service.vote;

import life.offonoff.ab.application.service.vote.criteria.VotingEndCriteria;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainer;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static life.offonoff.ab.domain.topic.TopicStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(TestVoteConfig.TestContainerVotingTopicConfig.class)
public class VotingTopicContainerServiceIntegrationTest {

    @Autowired
    VotingTopicService votingTopicContainerService;

    @MockBean
    VotingEndCriteria criteria;
    @MockBean
    VotingTopicContainer container;
    @MockBean
    TopicRepository topicRepository;
    @MockBean
    MemberRepository memberRepository;

    @Test
    @DisplayName("투표가 끝난 토픽은 status 수정 & Voting Result 매핑")
    void endVote_then_status_voting_result() {
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
        votingTopicContainerService.endVote(criteria);

        // then
        assertAll(
                () -> assertThat(topic.getStatus()).isEqualTo(NOTICED),
                () -> assertThat(topic.getVotingResult()).isNotNull()
        );
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
        when(memberRepository.findAllVotedTo(topic.getId())).thenReturn(voteMembers);

        // when
        votingTopicContainerService.endVote(criteria);

        // then
        assertAll(
                () -> assertThat(member.getNotifications().size()).isGreaterThan(0)
        );
    }
//    @Test
//    @DisplayName("투표가 끝난 토픽 수 만큼 이벤트 발행")
//    void event_publish() {
//        // given
//        LocalDateTime standard = LocalDateTime.now();
//        LocalDateTime beforeStandard = standard.minusDays(1);
//
//        VotingTopic topic1 = new VotingTopic(1L, beforeStandard);
//        VotingTopic topic2 = new VotingTopic(2L, beforeStandard);
//        List<VotingTopic> topics = List.of(topic1, topic2);
//
//        when(container.getVotingEnded(any())).thenReturn(topics);
//        doNothing().when(topicRepository).updateStatus(any(), any());
//        when(topicRepository.findById(anyLong())).thenReturn(Optional.of(any(Topic.class)));
//
//        // when
//        votingService.endVoting(criteria);
//
//        // then
//        verify(eventPublisher, times(topics.size())).publishEvent(any(VotingEndEvent.class));
//    }
//
//    @Test
//    @DisplayName("토픽 조회하는 과정에서 예외 발생 시 이벤트 발행X")
//    void event_publish_when_exception_in_searching() {
//        // given
//        LocalDateTime standard = LocalDateTime.now();
//        when(container.getVotingEnded(any())).thenThrow(RuntimeException.class);
//
//        try {
//            // when
//            votingService.endVoting(criteria);
//        } catch (RuntimeException e) {
//            // then
//            verify(eventPublisher, never()).publishEvent(any(VotingEndEvent.class));
//        }
//    }
//
//    @Test
//    @DisplayName("토픽 status 수정 과정에서 예외 시에 수정 성공한 토픽만 이벤트 발행")
//    void event_publish_when_exception_in_update_status() {
//        // given
//        LocalDateTime standard = LocalDateTime.now();
//        LocalDateTime beforeStandard = standard.minusDays(1);
//
//        VotingTopic topic1 = new VotingTopic(1L, beforeStandard);
//        VotingTopic topic2 = new VotingTopic(2L, beforeStandard);
//        List<VotingTopic> topics = List.of(topic1, topic2);
//
//        when(container.getVotingEnded(any())).thenReturn(topics);
//        doThrow(RuntimeException.class).when(topicRepository).updateStatus(eq(topic2.topicId()), any());
//
//        try {
//            // when
//            votingService.endVoting(criteria);
//        } catch (RuntimeException e) {
//            // then
//            verify(eventPublisher, times(topics.size() - 1)).publishEvent(any(VotingEndEvent.class));
//        }
//    }
}