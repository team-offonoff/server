package life.offonoff.ab.domain.notice;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notice.NotificationType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(VOTING_RESULT_NOTIFICATION)
public class VotingResultNotification extends Notification {
    private Long topicId;
    private int totalVoteCount;

    public VotingResultNotification(Long topicId, int totalVoteCount) {
        this.topicId = topicId;
        this.totalVoteCount = totalVoteCount;
    }
}
