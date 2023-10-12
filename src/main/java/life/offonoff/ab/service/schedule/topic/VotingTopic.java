package life.offonoff.ab.service.schedule.topic;

import java.time.LocalDateTime;

public record VotingTopic(
        Long topicId,
        LocalDateTime deadline
) {

    public VotingTopic(Long topicId, LocalDateTime deadline) {
        this.topicId = topicId;
        this.deadline = deadline;
    }

    public boolean votingEnded(LocalDateTime time) {
        return !time.isBefore(deadline);
    }
}
