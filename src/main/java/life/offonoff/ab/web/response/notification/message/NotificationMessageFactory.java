package life.offonoff.ab.web.response.notification.message;

import life.offonoff.ab.domain.notification.DefaultNotification;
import life.offonoff.ab.domain.notification.Notification;
import life.offonoff.ab.domain.notification.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.notification.VoteResultNotification;

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

        throw new RuntimeException();
    }
}
