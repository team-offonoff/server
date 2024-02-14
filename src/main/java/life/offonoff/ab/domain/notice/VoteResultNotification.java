package life.offonoff.ab.domain.notice;

import jakarta.persistence.*;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.vote.VotingResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notice.NotificationType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(VOTING_CLOSED_NOTIFICATION)
public class VotingResultNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    private VotingResult votingResult;

    public VotingResultNotification(Member member, VotingResult votingResult) {
        super(member);
        this.votingResult = votingResult;
    }
}
