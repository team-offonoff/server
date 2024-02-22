package life.offonoff.ab.web.response.notice;

import life.offonoff.ab.domain.notice.Notification;
import life.offonoff.ab.web.response.notice.message.NoticeMessage;
import life.offonoff.ab.web.response.notice.message.NoticeMessageFactory;
import lombok.Getter;

@Getter
public class NoticeResponse {

    private String type;
    private Boolean checked;
    private NoticeMessage message;

    public NoticeResponse(Notification notification) {
        this.type = notification.getType();
        this.checked = notification.getChecked();
        this.message = NoticeMessageFactory.createNoticeMessage(notification);
    }

    public NoticeResponse(String type, Boolean checked, NoticeMessage message) {
        this.type = type;
        this.checked = checked;
        this.message = message;
    }
}
