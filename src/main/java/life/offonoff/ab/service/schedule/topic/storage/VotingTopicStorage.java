package life.offonoff.ab.service.schedule.topic.storage;

import life.offonoff.ab.service.schedule.topic.VotingTopic;

import java.util.List;
import java.util.function.Predicate;

/**
 * Voting Topic을 유지할 자료구조 추상화 (추후 redis 등으로 변경 가능)
 */
public interface VotingTopicStorage {

    void loadInVoting(List<VotingTopic> schedules);

    void add(VotingTopic schedule);

    void removeIf(Predicate<VotingTopic> predicate);

    boolean isEmpty();

    int size();

    VotingTopic front();

    VotingTopic popFront();
}
