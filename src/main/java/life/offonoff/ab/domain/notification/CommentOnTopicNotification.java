package life.offonoff.ab.domain.notification;

import jakarta.persistence.*;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static life.offonoff.ab.domain.notification.NotificationType.COMMENT_ON_TOPIC_NOTIFICATION;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(COMMENT_ON_TOPIC_NOTIFICATION)
public class CommentOnTopicNotification extends Notification {

    @ManyToOne
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    public CommentOnTopicNotification(Comment comment) {
        super(comment.getTopic()
                     .getAuthor());
        this.comment = comment;
    }

    @Override
    public String getType() {
        return COMMENT_ON_TOPIC_NOTIFICATION;
    }
}
