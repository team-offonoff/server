package life.offonoff.ab.application.service.vote;

import life.offonoff.ab.application.service.vote.criteria.VoteClosingCriteria;
import life.offonoff.ab.application.service.vote.votingtopic.container.VotingTopic;
import life.offonoff.ab.domain.topic.Topic;

public interface VotingTopicService {

    void startVote(VotingTopic votingTopic);

    void endVote(VoteClosingCriteria criteria);
}
