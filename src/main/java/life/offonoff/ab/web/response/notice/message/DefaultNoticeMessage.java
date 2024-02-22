package life.offonoff.ab.web.response.notice.message;

import life.offonoff.ab.domain.notice.DefaultNotification;
import life.offonoff.ab.web.response.notice.message.NoticeMessage;
import lombok.Getter;

import static life.offonoff.ab.domain.notice.NotificationType.DEFAULT;

public class DefaultNoticeMessage extends NoticeMessage {
    public DefaultNoticeMessage(DefaultNotification notification) {
        super(notification.getTitle(), notification.getContent());
    }
}
