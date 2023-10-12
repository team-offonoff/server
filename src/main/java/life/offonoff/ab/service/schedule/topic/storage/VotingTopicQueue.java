package life.offonoff.ab.service.schedule.topic.storage;

import life.offonoff.ab.service.schedule.topic.VotingTopic;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;

/**
 * <p>
 *     우선순위 큐(deadline 기준)로 Voting Topic 관리
 * </p>
 *
 *
 * {@link life.offonoff.ab.config.ScheduleConfig}에서 Bean 등록
 */
public class VotingTopicQueue implements VotingTopicStorage {

    private final Queue<VotingTopic> storage;

    public VotingTopicQueue(Comparator<VotingTopic> comparator) {
        this.storage = new PriorityQueue<>(comparator);
    }

    @Override
    public void loadInVoting(List<VotingTopic> schedules) {
        this.storage.addAll(schedules);
    }

    @Override
    public void add(VotingTopic schedule) {
        storage.offer(schedule);
    }

    @Override
    public void remove(VotingTopic votingTopic) {
        storage.removeIf(votingTopic::equals);
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public VotingTopic front() {
        return storage.peek();
    }

    @Override
    public VotingTopic popFront() {
        return storage.poll();
    }

    @Override
    public boolean contains(VotingTopic votingTopic) {
        return storage.contains(votingTopic);
    }
}
