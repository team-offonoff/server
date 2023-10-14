package life.offonoff.ab.application.schedule.topic;

import java.time.LocalDateTime;

public record VotingTopic(
        Long topicId,
        LocalDateTime deadline
) {

    public boolean deadlinePassed(LocalDateTime time) {
        return deadline.isBefore(time);
    }
}
