package life.offonoff.ab.web.response.notice;

import life.offonoff.ab.domain.notice.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.topic.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static life.offonoff.ab.domain.notice.NotificationType.VOTE_COUNT_ON_TOPIC_NOTIFICATION;
import static life.offonoff.ab.domain.notice.NotificationType.VOTE_RESULT_NOTIFICATION;

@Getter
public class VoteCountOnTopicNoticeResponse extends NoticeResponse {

    private Long topicId;
    private String topicTitle;
    private int totalVoteCount;

    public VoteCountOnTopicNoticeResponse(VoteCountOnTopicNotification notification) {
        super(VOTE_COUNT_ON_TOPIC_NOTIFICATION, notification.getChecked());

        Topic topic = notification.getTopic();
        this.topicId = topic.getId();
        this.topicTitle = topic.getTitle();

        this.totalVoteCount = notification.getTotalVoteCount();
    }

    public VoteCountOnTopicNoticeResponse(Boolean checked, Long topicId, String topicTitle, int totalVoteCount) {
        super(VOTE_COUNT_ON_TOPIC_NOTIFICATION, checked);
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.totalVoteCount = totalVoteCount;
    }
}
