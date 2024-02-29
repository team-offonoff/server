package life.offonoff.ab.web.response.notification.message;

import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.notification.LikeInCommentNotification;
import lombok.Getter;

import static life.offonoff.ab.web.response.notification.message.NotificationMessageTemplate.LIKE_ON_COMMENT_TITLE;

@Getter
public class LikeInCommentNotificationMessage extends NotificationMessage {

    private final Long topicId;
    private final Long commentId;

    public LikeInCommentNotificationMessage(String title, String content, Long topicId, Long commentId) {
        super(title, content);
        this.topicId = topicId;
        this.commentId = commentId;
    }

    public LikeInCommentNotificationMessage(LikeInCommentNotification notification) {
        super(LIKE_ON_COMMENT_TITLE, notification.getComment()
                                                 .getTopic()
                                                 .getTitle());

        Comment comment = notification.getComment();

        this.commentId = comment.getId();
        this.topicId = comment.getTopic()
                              .getId();
    }
}
