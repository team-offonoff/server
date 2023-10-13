package life.offonoff.ab.application.schedule.topic.storage;

import life.offonoff.ab.application.schedule.topic.VotingTopic;

import java.util.List;
import java.util.function.Predicate;

/**
 * Voting Topic을 유지할 자료구조 추상화 (추후 redis 등으로 변경 가능)
 */
public interface VotingTopicStorage {

    void loadInVoting(List<VotingTopic> topics);

    void add(VotingTopic votingTopic);

    void remove(VotingTopic votingTopic);

    boolean isEmpty();

    int size();

    VotingTopic front();

    List<VotingTopic> popAllIf(Predicate<VotingTopic> predicate);

    boolean contains(VotingTopic votingTopic);
}
