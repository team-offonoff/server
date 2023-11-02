package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.domain.vote.VotingResult;

public record VotingEndEvent(
        Long topicId,
        VotingResult result
) {
}
