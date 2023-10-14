package life.offonoff.ab.service.schedule.topic.storage;

import life.offonoff.ab.application.schedule.topic.VotingTopic;
import life.offonoff.ab.application.schedule.topic.storage.VotingTopicDeadlineQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class VotingTopicDeadlineQueueTest {

    private VotingTopicDeadlineQueue queue;
    private Comparator<VotingTopic> comparator
            = (t1, t2) -> t1.deadline().compareTo(t2.deadline());

    @BeforeEach
    void beforeEach() {
        queue = new VotingTopicDeadlineQueue(comparator);
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
    @DisplayName("큐에 추가하면 contained")
    void insert_then_contains() {
        VotingTopic votingTopic = new VotingTopic(1L, LocalDateTime.now());
        queue.add(votingTopic);

        assertThat(queue.contains(votingTopic)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않으면 not contains")
    void contains_non_match() {
        VotingTopic votingTopic = new VotingTopic(1L, LocalDateTime.now());

        assertThat(queue.contains(votingTopic)).isFalse();
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
    @DisplayName("불러온 토픽들은 큐에 load")
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
    @DisplayName("빈 큐에서 popAllIf 시 null 리턴")
    void popFront_when_empty() {
        assertThat(queue.popAllIf(
                        votingTopic -> votingTopic.deadlinePassed(LocalDateTime.now())
                )
        ).isEmpty();
    }

    @Test
    @DisplayName("큐에서 popAllIf 시 만족하는 VotingTopic들 리턴")
    void popFront_not_empty() {
        // given
        LocalDateTime now = LocalDateTime.now();
        VotingTopic topic1 = new VotingTopic(1L, now);
        VotingTopic topic2 = new VotingTopic(2L, now.plusHours(2));

        List<VotingTopic> topics = List.of(topic1, topic2);
        queue.loadInVoting(topics);

        // when
        List<VotingTopic> front = queue.popAllIf(vt -> vt.deadlinePassed(now.plusHours(1)));
        assertAll(
                () -> assertThat(front).isNotNull(),
                () -> assertThat(queue.size()).isEqualTo(topics.size() - 1)
        );
    }

    @Test
    @DisplayName("같은 VoticTopic이 저장돼있으면 삭제")
    void remove() {
        // given
        VotingTopic votingTopic = new VotingTopic(1L, LocalDateTime.now());
        queue.add(votingTopic);

        // when
        queue.remove(votingTopic);

        // then
        assertThat(queue.contains(votingTopic)).isFalse();
    }
}