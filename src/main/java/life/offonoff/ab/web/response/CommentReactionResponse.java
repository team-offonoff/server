package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentReactionResponse {

    private Integer likeCount;
    private Integer hateCount;
    private Boolean liked;
    private Boolean hated;

    public static CommentReactionResponse from(Comment comment, Member member) {
        return new CommentReactionResponse(
                comment.getLikeCount(),
                comment.getHateCount(),
                member.likeAlready(comment),
                member.hateAlready(comment)
        );
    }

    public static CommentReactionResponse from(Comment comment) {
        return new CommentReactionResponse(
                comment.getLikeCount(),
                comment.getHateCount(),
                false,
                false
        );
    }
}
