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

    private final Queue<VotingTopic> schedules;

    public VotingTopicQueue(Comparator<VotingTopic> comparator) {
        this.schedules = new PriorityQueue<>(comparator);
    }

    @Override
    public void loadInVoting(List<VotingTopic> schedules) {
        this.schedules.addAll(schedules);
    }

    @Override
    public void add(VotingTopic schedule) {
        schedules.offer(schedule);
    }

    @Override
    public void removeIf(Predicate<VotingTopic> predicate) {
        schedules.removeIf(predicate);
    }

    @Override
    public boolean isEmpty() {
        return schedules.isEmpty();
    }

    @Override
    public int size() {
        return schedules.size();
    }

    @Override
    public VotingTopic front() {
        return schedules.peek();
    }

    @Override
    public VotingTopic popFront() {
        return schedules.poll();
    }
}
