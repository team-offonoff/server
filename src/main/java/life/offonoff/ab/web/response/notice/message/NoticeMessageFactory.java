package life.offonoff.ab.web.response.notice.message;

import life.offonoff.ab.domain.notice.DefaultNotification;
import life.offonoff.ab.domain.notice.Notification;
import life.offonoff.ab.domain.notice.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.notice.VoteResultNotification;

public class NoticeMessageFactory {

    public static NoticeMessage createNoticeMessage(Notification notification) {
        if (notification instanceof DefaultNotification) {
            return new DefaultNoticeMessage((DefaultNotification) notification);
        }

        if (notification instanceof VoteResultNotification) {
            return new VoteResultNoticeMessage((VoteResultNotification) notification);
        }

        if (notification instanceof VoteCountOnTopicNotification) {
            return new VoteCountOnTopicNoticeMessage((VoteCountOnTopicNotification) notification);
        }

        throw new RuntimeException();
    }
}
