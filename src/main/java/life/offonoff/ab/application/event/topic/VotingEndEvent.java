package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VotingResult;

public record VotingEndEvent(
        Topic topic,
        VotingResult result
) {
}
