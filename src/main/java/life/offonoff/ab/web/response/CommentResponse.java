package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CommentResponse {

    private Long commentId;
    private Long topicId;
    private MemberResponse writer;
    private ChoiceOption writersVotedOption;
    private String content;
    private CommentReactionResponse commentReaction;
    private Long createdAt;

    public static CommentResponse from(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentResponse(comment.getId(),
                comment.getTopic().getId(),
                MemberResponse.from(comment.getWriter()),
                comment.getWritersVotedOption(),
                comment.getContent(),
                CommentReactionResponse.from(comment),
                comment.getCreatedSecond());
    }

    public static CommentResponse from(Comment comment, Member member) {
        return new CommentResponse(comment.getId(),
                comment.getTopic().getId(),
                MemberResponse.from(comment.getWriter()),
                comment.getWritersVotedOption(),
                comment.getContent(),
                CommentReactionResponse.from(comment, member),
                comment.getCreatedSecond());
    }

}
