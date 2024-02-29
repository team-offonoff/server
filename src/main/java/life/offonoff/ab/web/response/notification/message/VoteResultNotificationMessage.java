package life.offonoff.ab.web.response.notification.message;

import life.offonoff.ab.domain.notification.VoteResultNotification;
import lombok.Getter;

import static life.offonoff.ab.web.response.notification.message.NotificationMessageTemplate.VOTE_RESULT_TITLE;

@Getter
public class VoteResultNotificationMessage extends NotificationMessage {

    private Long topicId;

    public VoteResultNotificationMessage(String content, Long topicId) {
        super(VOTE_RESULT_TITLE, content);
        this.topicId = topicId;
    }

    public VoteResultNotificationMessage(VoteResultNotification notification) {
        super(VOTE_RESULT_TITLE, notification.getTopic()
                                             .getTitle());
        this.topicId = notification.getTopic()
                                   .getId();
    }
}
