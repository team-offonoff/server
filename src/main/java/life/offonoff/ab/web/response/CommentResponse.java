package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentResponse {

    private Long commentId;
    private Long topicId;
    private MemberResponse writer;
    private String content;
    private Integer likeCount;
    private Integer hateCount;
    private Boolean liked;
    private Boolean hated;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentResponse(comment.getId(),
                comment.getTopic().getId(),
                MemberResponse.from(comment.getWriter()),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getHateCount(),
                false,
                false,
                comment.getCreatedAt());
    }

    public static CommentResponse from(Comment comment, Member member) {
        return new CommentResponse(comment.getId(),
                comment.getTopic().getId(),
                MemberResponse.from(comment.getWriter()),
                comment.getContent(),
                comment.getLikeCount(),
                comment.getHateCount(),
                member.likeAlready(comment),
                member.hateAlready(comment),
                comment.getCreatedAt());
    }

}
