package life.offonoff.ab.application.service.vote;

import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainer;
import life.offonoff.ab.application.service.vote.criteria.VoteClosingCriteria;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopicContainerService;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.repository.topic.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VotingTopicContainerServiceTest {

    @InjectMocks
    VotingTopicContainerService votingTopicContainerService;
    @Mock
    VotingTopicContainer container;
    @Mock
    TopicRepository topicRepository;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @Mock
    VoteClosingCriteria criteria;

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

    // 테스트 환경에서 ApplicationEventPublisher MockBean이 DI가 되지 않아서 일단 리플렉션으로 DI했습니다...
    private void setEventPublisher(VotingTopicContainerService votingTopicContainerService, ApplicationEventPublisher eventPublisher) {
        ReflectionTestUtils.setField(votingTopicContainerService, "eventPublisher", eventPublisher);
    }
}