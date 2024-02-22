package life.offonoff.ab.domain.notice;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.vote.VoteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notice.NotificationType.COMMENT_ON_TOPIC_NOTIFICATION;
// TODO:수정대상
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(COMMENT_ON_TOPIC_NOTIFICATION)
public class CommentOnTopicNotification extends Notification {
    @Override
    public String getType() {
        return COMMENT_ON_TOPIC_NOTIFICATION;
    }
}
