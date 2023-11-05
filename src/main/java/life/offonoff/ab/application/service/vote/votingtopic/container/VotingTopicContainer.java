package life.offonoff.ab.application.service.vote.votingtopic.container;

import life.offonoff.ab.application.service.vote.criteria.VotingEndCriteria;
import life.offonoff.ab.application.service.vote.votingtopic.container.store.VotingTopicStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class VotingTopicContainer {

    private final VotingTopicStorage storage;

    public void load(List<VotingTopic> topics) {
        this.storage.loadInVoting(topics);

        log.info("Resume Voting, loaded Topic : {}", topics.size());
    }

    public void insert(VotingTopic votingTopic) {
        remove(votingTopic);
        storage.add(votingTopic);
        log.info("new TopicSchedules({}) added, total schedules : {}", votingTopic, storage.size());
    }

    public void remove(VotingTopic votingTopic) {
        storage.remove(votingTopic);
    }

    public int size() {
        return storage.size();
    }

    /**
     * 투표가 끝난 토픽 반환
     */
    public List<VotingTopic> getVotingEnded(VotingEndCriteria criteria) {
        return storage.popAllIf(criteria::check);
    }
}
