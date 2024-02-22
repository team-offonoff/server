package life.offonoff.ab.web.response.notice;

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
    private String topicTitle;
    private int totalVoteCount;

    public VoteCountOnTopicNoticeMessage(VoteCountOnTopicNotification notification) {
        Topic topic = notification.getTopic();
        this.topicId = topic.getId();
        this.topicTitle = topic.getTitle();

        this.totalVoteCount = notification.getTotalVoteCount();
        super(VOTE_COUNT_ON_TOPIC_TITLE, );
    }

    public VoteCountOnTopicNoticeMessage(Boolean checked, Long topicId, String topicTitle, int totalVoteCount) {
        super(VOTE_COUNT_ON_TOPIC_NOTIFICATION, checked);
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.totalVoteCount = totalVoteCount;
    }
}
