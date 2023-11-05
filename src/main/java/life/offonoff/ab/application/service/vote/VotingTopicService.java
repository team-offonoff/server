package life.offonoff.ab.application.service.vote;

import life.offonoff.ab.application.service.vote.criteria.VotingEndCriteria;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VotingResult;

public interface VotingTopicService {

    default VotingResult aggregateVote(Topic topic) {
        VotingResult result = new VotingResult();
        result.setTopic(topic);
        return result;
    }

    void startVote(VotingTopic votingTopic);

    void endVote(VotingEndCriteria criteria);
}
