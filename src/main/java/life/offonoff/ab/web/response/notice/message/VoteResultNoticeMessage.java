package life.offonoff.ab.web.response.notice;

import life.offonoff.ab.domain.notice.VoteResultNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.VoteResult;
import lombok.Getter;

import static life.offonoff.ab.domain.notice.NotificationType.VOTE_RESULT_NOTIFICATION;

@Getter
public class VoteResultNoticeMessage extends NoticeResponse {

    private Long topicId;
    private String topicTitle;

    public VoteResultNoticeMessage(VoteResultNotification notification) {
        super(VOTE_RESULT_NOTIFICATION, notification.getChecked());

        VoteResult voteResult = notification.getVoteResult();
        Topic topic = voteResult.getTopic();

        this.topicId = topic.getId();
        this.topicTitle = topic.getTitle();
    }

    public VoteResultNoticeMessage(Boolean checked, Long topicId, String topicTitle) {
        super(VOTE_RESULT_NOTIFICATION, checked);
        this.topicId = topicId;
        this.topicTitle = topicTitle;
    }
}
