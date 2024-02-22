package life.offonoff.ab.web.response.notice.message;

import lombok.Getter;

@Getter
public abstract class NoticeMessage {

    private final String title;
    private final String content;

    public NoticeMessage(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
