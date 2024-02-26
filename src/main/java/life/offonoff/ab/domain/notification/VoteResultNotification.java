package life.offonoff.ab.domain.notification;

import jakarta.persistence.*;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.vote.VoteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notification.NotificationType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(VOTE_RESULT_NOTIFICATION)
public class VoteResultNotification extends Notification {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vote_result_id")
    private VoteResult voteResult;

    public VoteResultNotification(Member member, VoteResult voteResult) {
        super(member);
        this.voteResult = voteResult;
    }

    @Override
    public String getType() {
        return VOTE_RESULT_NOTIFICATION;
    }
}
