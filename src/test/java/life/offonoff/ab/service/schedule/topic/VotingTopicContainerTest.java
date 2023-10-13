package life.offonoff.ab.service.schedule.topic;

import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.application.schedule.topic.VotingTopic;
import life.offonoff.ab.application.schedule.topic.VotingTopicContainer;
import life.offonoff.ab.application.schedule.topic.storage.VotingTopicStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingTopicContainerTest {

    @InjectMocks
    VotingTopicContainer container;

    @Mock
    VotingTopicStorage storage;
    @Mock
    TopicRepository topicRepository;

    @Test
    @DisplayName("빈 컨테이너는 사이즈가 0")
    void empty_size() {
        assertThat(container.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("VotingTopic을 추가하면 사이즈 증가")
    void insert_토픽() {
        // given
        VotingTopic votingTopic = new VotingTopic(1L, LocalDateTime.now());
        when(storage.size()).thenReturn(1);

        // when
        container.insert(votingTopic);

        // then
        assertThat(container.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("컨테이너 초기화하면 토픽 load")
    void init() {
        // given
        VotingTopic votingTopic1 = new VotingTopic(1L, LocalDateTime.now());
        VotingTopic votingTopic2 = new VotingTopic(2L, LocalDateTime.now());
        List<VotingTopic> topics = List.of(votingTopic1, votingTopic2);

        when(topicRepository.findAllInVoting(any())).thenReturn(topics);
        when(storage.size()).thenReturn(topics.size());

        // when
        container.init();

        // then
        assertThat(container.size()).isEqualTo(topics.size());
    }

    @Test
    @DisplayName("입력 시각 기준으로 투표가 끝난 토픽들 반환")
    void get_voting_ended() {
        // given
        LocalDateTime standard = LocalDateTime.now();
        LocalDateTime beforeStandard = standard.minusHours(1);

        VotingTopic votingTopic1 = new VotingTopic(1L, beforeStandard);
        VotingTopic votingTopic2 = new VotingTopic(2L, beforeStandard);
        List<VotingTopic> topics = List.of(votingTopic1, votingTopic2);

        when(storage.popAllIf(any())).thenReturn(topics);

        // when
        List<VotingTopic> votingEnded = container.getVotingEnded(standard);

        // then
        assertThat(votingEnded).containsExactlyElementsOf(topics);
    }
}