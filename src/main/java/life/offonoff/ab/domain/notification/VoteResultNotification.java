package life.offonoff.ab.domain.notification;

import jakarta.persistence.*;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static life.offonoff.ab.domain.notification.NotificationType.*;
import static life.offonoff.ab.domain.notification.ReceiverType.AUTHOR;
import static life.offonoff.ab.domain.notification.ReceiverType.VOTER;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(VOTE_RESULT_NOTIFICATION)
public class VoteResultNotification extends Notification {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Topic topic;

    public VoteResultNotification(String receiverType, Member member, Topic topic) {
        super(receiverType, member);
        this.topic = topic;
    }

    public static VoteResultNotification createForVoter(Member voter, Topic topic) {
        return new VoteResultNotification(VOTER, voter, topic);
    }

    public static VoteResultNotification createForAuthor(Topic topic) {
        return new VoteResultNotification(AUTHOR, topic.getAuthor(), topic);
    }

    @Override
    public String getType() {
        return VOTE_RESULT_NOTIFICATION;
    }
}
