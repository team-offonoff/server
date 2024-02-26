package life.offonoff.ab.web.response.notification.message;

import life.offonoff.ab.domain.notification.DefaultNotification;

public class DefaultNotificationMessage extends NotificationMessage {
    public DefaultNotificationMessage(DefaultNotification notification) {
        super(notification.getTitle(), notification.getContent());
    }
}
