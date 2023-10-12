package life.offonoff.ab.service.schedule.topic.storage;

import life.offonoff.ab.service.schedule.topic.VotingTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class VotingTopicQueueTest {

    private VotingTopicQueue queue;
    private Comparator<VotingTopic> comparator
            = (t1, t2) -> t1.deadline().compareTo(t2.deadline());

    @BeforeEach
    void beforeEach() {
        queue = new VotingTopicQueue(comparator);
    }

    @Test
    @DisplayName("큐 초기 사이즈는 0")
    void size_initial() {
        assertThat(queue.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("큐에 추가되면 사이즈는 증가")
    void size_increase() {
        queue.add(new VotingTopic(1L, LocalDateTime.now()));

        assertThat(queue.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("빈 큐는 empty")
    void empty() {
        assertThat(queue.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("비지않은 큐는 not empty")
    void not_empty() {
        queue.add(new VotingTopic(1L, LocalDateTime.now()));

        assertThat(queue.isEmpty()).isFalse();
    }

    @Test
    void load_VotingTopics() {
        // given
        VotingTopic topic1 = new VotingTopic(1L, LocalDateTime.now());
        VotingTopic topic2 = new VotingTopic(2L, LocalDateTime.now());

        List<VotingTopic> topicList = List.of(topic1, topic2);

        // when
        queue.loadInVoting(topicList);

        // then
        assertThat(queue.size()).isEqualTo(topicList.size());
    }

    @Test
    @DisplayName("빈 큐에서 popFront 시 null 리턴")
    void popFront_when_empty() {
        assertThat(queue.popFront()).isNull();
    }

    @Test
    @DisplayName("큐에서 popFront 시 가장 앞 VotingTopic 리턴")
    void popFront_not_empty() {
        // given
        LocalDateTime now = LocalDateTime.now();
        VotingTopic topic1 = new VotingTopic(1L, now);
        VotingTopic topic2 = new VotingTopic(2L, now.plusHours(1));

        List<VotingTopic> topics = List.of(topic1, topic2);
        queue.loadInVoting(topics);

        // when
        VotingTopic front = queue.popFront();
        assertAll(
                () -> assertThat(front).isNotNull(),
                () -> assertThat(queue.size()).isEqualTo(topics.size() - 1)
        );
    }
}