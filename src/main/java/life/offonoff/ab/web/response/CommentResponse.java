package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentResponse {

    private Long commentId;
    private Long topicId;
    private Long writerId;
    private String writerNickname;
    private String writerProfileImageUrl;
    private String content;
    private Integer likes;
    private Integer hates;

    public CommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.topicId = comment.getTopic().getId();
        this.writerId = comment.getWriter().getId();
        this.writerNickname = comment.getWriter().getNickname();
        this.writerProfileImageUrl = comment.getWriter().getProfileImageUrl();
        this.content = comment.getContent();
        this.likes = comment.getLikeCount();
        this.hates = comment.getHateCount();
    }

}
