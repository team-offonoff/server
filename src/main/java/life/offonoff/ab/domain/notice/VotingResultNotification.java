package life.offonoff.ab.domain.notice;

import jakarta.persistence.*;
import life.offonoff.ab.domain.vote.VotingResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notice.NotificationType.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@DiscriminatorValue(VOTING_RESULT_NOTIFICATION)
public class VotingResultNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    private VotingResult votingResult;
}
