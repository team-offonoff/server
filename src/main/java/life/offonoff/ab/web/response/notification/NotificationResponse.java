package life.offonoff.ab.web.response.notification;

import life.offonoff.ab.domain.notification.Notification;
import life.offonoff.ab.web.response.notification.message.NotificationMessage;
import life.offonoff.ab.web.response.notification.message.NotificationMessageFactory;
import lombok.Getter;

@Getter
public class NotificationResponse {

    private String type;
    private Boolean checked;
    private NotificationMessage message;

    public NotificationResponse(Notification notification) {
        this.type = notification.getType();
        this.checked = notification.getChecked();
        this.message = NotificationMessageFactory.createNoticeMessage(notification);
    }

    public NotificationResponse(String type, Boolean checked, NotificationMessage message) {
        this.type = type;
        this.checked = checked;
        this.message = message;
    }
}
