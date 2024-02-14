package life.offonoff.ab.domain.notice;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.notice.NotificationType.DEFAULT;
import static life.offonoff.ab.domain.notice.NotificationType.VOTE_COUNT_ON_TOPIC_NOTIFICATION;

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
}
