package life.offonoff.ab.domain.notification;

import jakarta.persistence.*;
import life.offonoff.ab.domain.comment.Comment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static life.offonoff.ab.domain.notification.NotificationType.LIKE_IN_COMMENT_NOTIFICATION;
// TODO:수정대상
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(LIKE_IN_COMMENT_NOTIFICATION)
public class LikeInCommentNotification extends Notification {

    @ManyToOne
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    public LikeInCommentNotification(Comment comment) {
        super(comment.getWriter());

        this.comment = comment;
    }

    @Override
    public String getType() {
        return LIKE_IN_COMMENT_NOTIFICATION;
    }
}
