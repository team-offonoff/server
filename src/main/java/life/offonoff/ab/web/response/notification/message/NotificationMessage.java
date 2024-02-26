package life.offonoff.ab.web.response.notification.message;

import lombok.Getter;

@Getter
public abstract class NotificationMessage {

    private final String title;
    private final String content;

    public NotificationMessage(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
