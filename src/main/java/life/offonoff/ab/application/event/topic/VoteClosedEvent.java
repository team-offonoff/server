package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VoteResult;

public record VotingEndEvent(
        Topic topic,
        VoteResult result
) {
}
