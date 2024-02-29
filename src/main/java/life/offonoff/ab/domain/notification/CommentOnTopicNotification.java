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

    /**
     * @OneToOne 관계이지만 {@link LikeInCommentNotification}가 {@link Comment}와 @ManyToOne 이기에 @ManyToOne 설정 ({@link Notification} 의 상속 전략 : SINGLE_TABLE)
     */
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
