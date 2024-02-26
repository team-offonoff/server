package life.offonoff.ab.domain.notification;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notification.NotificationType.LIKE_IN_COMMENT_NOTIFICATION;
// TODO:수정대상
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(LIKE_IN_COMMENT_NOTIFICATION)
public class LikeInCommentNotification extends Notification {

    @Override
    public String getType() {
        return LIKE_IN_COMMENT_NOTIFICATION;
    }
}
