package life.offonoff.ab.domain.notification;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notification.NotificationType.COMMENT_ON_TOPIC_NOTIFICATION;
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
