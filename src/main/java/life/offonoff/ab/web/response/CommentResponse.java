package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentResponse {

    private Long commentId;
    private Long topicId;
    private MemberResponse writer;
    private String content;
    private Integer likes;
    private Integer hates;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.getId(),
                comment.getTopic().getId(),
                MemberResponse.from(comment.getWriter()),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getHateCount());
    }

}
