package life.offonoff.ab.web.response.notice;

import lombok.Getter;

@Getter
public abstract class NoticeResponse {

    private String type;
    private Boolean checked;

    public NoticeResponse(String type, Boolean checked) {
        this.type = type;
        this.checked = checked;
    }
}
