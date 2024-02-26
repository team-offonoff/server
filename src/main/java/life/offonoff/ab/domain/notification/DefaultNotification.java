package life.offonoff.ab.domain.notification;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import life.offonoff.ab.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notification.NotificationType.DEFAULT;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(DEFAULT)
public class DefaultNotification extends Notification {

    private String title;
    private String content;

    public DefaultNotification(Member receiver, String title, String content) {
        super(receiver);

        this.title = title;
        this.content = content;
    }

    @Override
    public String getType() {
        return DEFAULT;
    }
}
