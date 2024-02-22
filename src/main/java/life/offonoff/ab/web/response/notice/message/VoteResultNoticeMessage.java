package life.offonoff.ab.web.response.notice.message;

import life.offonoff.ab.domain.notice.VoteResultNotification;
import lombok.Getter;

import static life.offonoff.ab.web.response.notice.message.NoticeMessageTemplate.VOTE_RESULT_TITLE;

@Getter
public class VoteResultNoticeMessage extends NoticeMessage {

    private Long topicId;

    public VoteResultNoticeMessage(String content, Long topicId) {
        super(VOTE_RESULT_TITLE, content);
        this.topicId = topicId;
    }

    public VoteResultNoticeMessage(VoteResultNotification notification) {
        super(VOTE_RESULT_TITLE, notification.getVoteResult()
                                             .getTopic()
                                             .getTitle());
        this.topicId = notification.getVoteResult()
                                   .getTopic()
                                   .getId();
    }
}
