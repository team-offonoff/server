package life.offonoff.ab.web.response.notification.message;

import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.notification.CommentOnTopicNotification;
import life.offonoff.ab.domain.notification.VoteCountOnTopicNotification;
import lombok.Getter;

import static life.offonoff.ab.web.response.notification.message.NotificationMessageTemplate.COMMENT_ON_TOPIC_TITLE;
import static life.offonoff.ab.web.response.notification.message.NotificationMessageTemplate.VOTE_COUNT_ON_TOPIC_TITLE;

@Getter
public class CommentOnTopicNotificationMessage extends NotificationMessage {

    private Long topicId;
    private Long commentId;

    public CommentOnTopicNotificationMessage(String title, String content, Long topicId, Long commentId) {
        super(title, content);
        this.topicId = topicId;
        this.commentId = commentId;
    }

    public CommentOnTopicNotificationMessage(CommentOnTopicNotification notification) {
        super(COMMENT_ON_TOPIC_TITLE, getCommentedTopicTitle(notification.getComment()));

        Comment comment = notification.getComment();

        this.commentId = comment.getId();
        this.topicId = comment.getTopic()
                              .getId();
    }

    private static String getCommentedTopicTitle(Comment comment) {
        return comment.getTopic()
                      .getTitle();
    }
}
