package life.offonoff.ab.web.response.notice;

import life.offonoff.ab.domain.notice.DefaultNotification;
import life.offonoff.ab.domain.notice.Notification;
import life.offonoff.ab.domain.notice.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.notice.VoteResultNotification;

public class NoticeResponseFactory {

    public static NoticeResponse createNoticeResponse(Notification notification) {
        if (notification instanceof DefaultNotification) {
            return new DefaultNoticeResponse((DefaultNotification) notification);
        }

        if (notification instanceof VoteResultNotification) {
            return new VoteResultNoticeResponse((VoteResultNotification) notification);
        }

        if (notification instanceof VoteCountOnTopicNotification) {
            return new VoteCountOnTopicNoticeResponse((VoteCountOnTopicNotification) notification);
        }

        throw new RuntimeException();
    }
}
