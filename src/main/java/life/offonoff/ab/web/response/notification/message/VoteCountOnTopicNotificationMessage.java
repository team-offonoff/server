package life.offonoff.ab.web.response.notification.message;

import life.offonoff.ab.domain.notification.VoteCountOnTopicNotification;
import lombok.Getter;

import static life.offonoff.ab.web.response.notification.message.NotificationMessageTemplate.VOTE_COUNT_ON_TOPIC_TITLE;

@Getter
public class VoteCountOnTopicNotificationMessage extends NotificationMessage {

    private Long topicId;

    public VoteCountOnTopicNotificationMessage(String title, String content, Long topicId) {
        super(title, content);
        this.topicId = topicId;
    }

    public VoteCountOnTopicNotificationMessage(VoteCountOnTopicNotification notification) {
        super(VOTE_COUNT_ON_TOPIC_TITLE.formatted(notification.getTopic().getVoteCount()),
                notification.getTopic().getTitle());

        this.topicId = notification.getTopic()
                                   .getId();
    }
}
