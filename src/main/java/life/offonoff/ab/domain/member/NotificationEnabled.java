package life.offonoff.ab.domain.member;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class NotificationEnabled {
    private boolean voteResult;
    private boolean likeInComment;
    private boolean voteCountOnTopic;
    private boolean commentOnTopic;

    public NotificationEnabled(boolean voteResult, boolean likeInComment, boolean voteCountOnTopic, boolean commentOnTopic) {
        this.voteResult = voteResult;
        this.likeInComment = likeInComment;
        this.voteCountOnTopic = voteCountOnTopic;
        this.commentOnTopic = commentOnTopic;
    }

    public static NotificationEnabled allEnabled() {
        return new NotificationEnabled(true, true, true, true);
    }

    public boolean listeningVoteResult() {
        return this.voteResult;
    }

    public boolean listeningLikeInComment() {
        return this.likeInComment;
    }

    public boolean listeningVoteCountOnTopic() {
        return this.voteCountOnTopic;
    }

    public boolean listeningCommentOnTopic() {
        return this.commentOnTopic;
    }
}
