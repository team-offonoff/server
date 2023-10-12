package life.offonoff.ab.service.schedule.topic;

import java.time.LocalDateTime;

public record VotingTopic(
        Long topicId,
        LocalDateTime deadline
) {

    public boolean votingEnded(LocalDateTime time) {
        return !time.isBefore(deadline);
    }
}
