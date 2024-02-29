package life.offonoff.ab.web.response.notification.message;

import life.offonoff.ab.domain.notification.*;

import static life.offonoff.ab.domain.notification.NotificationType.*;

public class NotificationMessageFactory {

    public static NotificationMessage createNoticeMessage(Notification notification) {
        String type = notification.getType();

        if (type.equals(DEFAULT)) {
            return new DefaultNotificationMessage((DefaultNotification) notification);
        }

        if (type.equals(VOTE_RESULT_NOTIFICATION)) {
            return new VoteResultNotificationMessage((VoteResultNotification) notification);
        }

        if (type.equals(VOTE_COUNT_ON_TOPIC_NOTIFICATION)) {
            return new VoteCountOnTopicNotificationMessage((VoteCountOnTopicNotification) notification);
        }

        if (type.equals(COMMENT_ON_TOPIC_NOTIFICATION)) {
            return new CommentOnTopicNotificationMessage((CommentOnTopicNotification) notification);
        }

        if (type.equals(LIKE_IN_COMMENT_NOTIFICATION)) {
            return new LikeInCommentNotificationMessage((LikeInCommentNotification) notification);
        }

        throw new RuntimeException();
    }
}
