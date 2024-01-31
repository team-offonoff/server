package life.offonoff.ab.web.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VoteResponse {

    private final CommentResponse latestComment;

    public static VoteResponse from(CommentResponse comment) {
        return new VoteResponse(comment);
    }
}
