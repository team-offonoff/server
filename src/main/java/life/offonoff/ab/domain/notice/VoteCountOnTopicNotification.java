package life.offonoff.ab.domain.notice;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VoteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notice.NotificationType.VOTE_COUNT_ON_TOPIC_NOTIFICATION;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(VOTE_COUNT_ON_TOPIC_NOTIFICATION)
public class VoteCountOnTopicNotification extends Notification {

    @ManyToOne(fetch = FetchType.EAGER)
    private Topic topic;
    private int totalVoteCount;

    public VoteCountOnTopicNotification(Member receiver, Topic topic) {
        super(receiver);

        this.topic = topic;
        this.totalVoteCount = topic.getVoteCount();
    }
}