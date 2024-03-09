package life.offonoff.ab.web.response.notification;

import life.offonoff.ab.domain.notification.Notification;
import life.offonoff.ab.web.response.notification.message.NotificationMessage;
import life.offonoff.ab.web.response.notification.message.NotificationMessageFactory;
import lombok.Getter;

@Getter
public class NotificationResponse {

    private Long id;
    private String type;
    private String receiverType;
    private Boolean isRead;
    private NotificationMessage message;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.receiverType = notification.getReceiverType();
        this.isRead = notification.getIsRead();
        this.message = NotificationMessageFactory.createNoticeMessage(notification);
    }

    public NotificationResponse(Long id, String type, String receiverType, Boolean isRead, NotificationMessage message) {
        this.id = id;
        this.type = type;
        this.receiverType = receiverType;
        this.isRead = isRead;
        this.message = message;
    }
}
