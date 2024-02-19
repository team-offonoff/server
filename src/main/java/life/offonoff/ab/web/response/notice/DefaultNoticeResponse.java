package life.offonoff.ab.web.response.notice;

import life.offonoff.ab.domain.notice.DefaultNotification;
import lombok.Getter;

import static life.offonoff.ab.domain.notice.NotificationType.DEFAULT;

@Getter
public class DefaultNoticeResponse extends NoticeResponse {

    private final String title;
    private final String content;

    public DefaultNoticeResponse(DefaultNotification notification) {
        super(DEFAULT, notification.getChecked());

        this.title = notification.getTitle();
        this.content = notification.getContent();
    }
}
