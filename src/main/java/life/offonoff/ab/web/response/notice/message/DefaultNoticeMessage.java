package life.offonoff.ab.web.response.notice;

import life.offonoff.ab.domain.notice.DefaultNotification;
import life.offonoff.ab.web.response.notice.message.NoticeMessage;
import lombok.Getter;

import static life.offonoff.ab.domain.notice.NotificationType.DEFAULT;

@Getter
public class DefaultNoticeMessage extends NoticeMessage {

    private final String title;
    private final String content;

    public DefaultNoticeMessage(DefaultNotification notification) {
        this.title = notification.getTitle();
        this.content = notification.getContent();
    }
}
