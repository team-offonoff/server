package life.offonoff.ab.web.response.notification.message;

import life.offonoff.ab.domain.notification.*;

public class NotificationMessageFactory {

    public static NotificationMessage createNoticeMessage(Notification notification) {
        if (notification instanceof DefaultNotification) {
            return new DefaultNotificationMessage((DefaultNotification) notification);
        }

        if (notification instanceof VoteResultNotification) {
            return new VoteResultNotificationMessage((VoteResultNotification) notification);
        }

        if (notification instanceof VoteCountOnTopicNotification) {
            return new VoteCountOnTopicNotificationMessage((VoteCountOnTopicNotification) notification);
        }

        if (notification instanceof CommentOnTopicNotification) {
            return new CommentOnTopicNotificationMessage((CommentOnTopicNotification) notification);
        }

        throw new RuntimeException();
    }
}
