package life.offonoff.ab.application.service.vote;

import life.offonoff.ab.application.service.vote.criteria.VoteClosingCriteria;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VoteResult;

public interface VotingTopicService {

    default VoteResult aggregateVote(Topic topic) {
        VoteResult result = new VoteResult();
        result.setTopic(topic);
        return result;
    }

    void startVote(VotingTopic votingTopic);

    void endVote(VoteClosingCriteria criteria);
}
