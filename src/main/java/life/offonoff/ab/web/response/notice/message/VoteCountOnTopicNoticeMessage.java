package life.offonoff.ab.web.response.notice.message;

import life.offonoff.ab.domain.notice.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.web.response.notice.message.NoticeMessage;
import life.offonoff.ab.web.response.notice.message.NoticeMessageTemplate;
import lombok.Getter;

import static life.offonoff.ab.domain.notice.NotificationType.VOTE_COUNT_ON_TOPIC_NOTIFICATION;
import static life.offonoff.ab.web.response.notice.message.NoticeMessageTemplate.VOTE_COUNT_ON_TOPIC_TITLE;

@Getter
public class VoteCountOnTopicNoticeMessage extends NoticeMessage {

    private Long topicId;

    public VoteCountOnTopicNoticeMessage(String title, String content, Long topicId) {
        super(title, content);
        this.topicId = topicId;
    }

    public VoteCountOnTopicNoticeMessage(VoteCountOnTopicNotification notification) {
        super(VOTE_COUNT_ON_TOPIC_TITLE.formatted(notification.getTopic().getVoteCount()),
                notification.getTopic().getTitle());

        this.topicId = notification.getTopic()
                                   .getId();
    }
}
