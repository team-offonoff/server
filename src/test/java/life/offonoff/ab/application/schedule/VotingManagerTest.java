package life.offonoff.ab.application.schedule;

import life.offonoff.ab.application.event.topic.VotingEndEvent;
import life.offonoff.ab.application.schedule.topic.VotingTopic;
import life.offonoff.ab.application.schedule.topic.VotingTopicContainer;
import life.offonoff.ab.application.schedule.topic.criteria.VotingEndCriteria;
import life.offonoff.ab.repository.topic.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
class VotingManagerTest {

    @Autowired
    VotingManager votingManager;
    @MockBean
    VotingTopicContainer container;
    @MockBean
    TopicRepository topicRepository;
    @MockBean
    ApplicationEventPublisher eventPublisher;
    @MockBean
    VotingEndCriteria criteria;

    @BeforeEach
    void beforeEach() {
        setEventPublisher(votingManager, eventPublisher);
    }

    @Test
    @DisplayName("투표가 끝난 토픽 수 만큼 이벤트 발행")
    void event_publish() {
        // given
        LocalDateTime standard = LocalDateTime.now();
        LocalDateTime beforeStandard = standard.minusDays(1);

        VotingTopic topic1 = new VotingTopic(1L, beforeStandard);
        VotingTopic topic2 = new VotingTopic(2L, beforeStandard);
        List<VotingTopic> topics = List.of(topic1, topic2);

        when(container.getVotingEnded(any())).thenReturn(topics);
        doNothing().when(topicRepository).updateStatus(any(), any());

        // when
        votingManager.endVoting(criteria);

        // then
        verify(eventPublisher, times(topics.size())).publishEvent(any(VotingEndEvent.class));
    }

    @Test
    @DisplayName("토픽 조회하는 과정에서 예외 발생 시 이벤트 발행X")
    void event_publish_when_exception_in_searching() {
        // given
        LocalDateTime standard = LocalDateTime.now();
        when(container.getVotingEnded(any())).thenThrow(RuntimeException.class);

        try {
            // when
            votingManager.endVoting(criteria);
        } catch (RuntimeException e) {
            // then
            verify(eventPublisher, never()).publishEvent(any(VotingEndEvent.class));
        }
    }

    @Test
    @DisplayName("토픽 status 수정 과정에서 예외 시에 수정 성공한 토픽만 이벤트 발행")
    void event_publish_when_exception_in_update_status() {
        // given
        LocalDateTime standard = LocalDateTime.now();
        LocalDateTime beforeStandard = standard.minusDays(1);

        VotingTopic topic1 = new VotingTopic(1L, beforeStandard);
        VotingTopic topic2 = new VotingTopic(2L, beforeStandard);
        List<VotingTopic> topics = List.of(topic1, topic2);

        when(container.getVotingEnded(any())).thenReturn(topics);
        doThrow(RuntimeException.class).when(topicRepository).updateStatus(eq(topic2.topicId()), any());

        try {
            // when
            votingManager.endVoting(criteria);
        } catch (RuntimeException e) {
            // then
            verify(eventPublisher, times(topics.size() - 1)).publishEvent(any(VotingEndEvent.class));
        }
    }

    // 테스트 환경에서 ApplicationEventPublisher MockBean이 DI가 되지 않아서 일단 리플렉션으로 DI했습니다...
    private void setEventPublisher(VotingManager votingManager, ApplicationEventPublisher eventPublisher) {
        ReflectionTestUtils.setField(votingManager, "eventPublisher", eventPublisher);
    }
}