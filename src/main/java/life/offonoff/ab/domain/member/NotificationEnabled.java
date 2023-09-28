package life.offonoff.ab.domain.member;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class NotificationEnabled {
    private boolean topicExpiration;
    private boolean likeInComment;
    private boolean voteOnTopic;
    private boolean commentOnTopic;

    public NotificationEnabled(boolean topicExpiration, boolean likeInComment, boolean voteOnTopic, boolean commentOnTopic) {
        this.topicExpiration = topicExpiration;
        this.likeInComment = likeInComment;
        this.voteOnTopic = voteOnTopic;
        this.commentOnTopic = commentOnTopic;
    }
}
