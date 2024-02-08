package life.offonoff.ab.web.response;

import life.offonoff.ab.web.response.topic.TopicResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VoteResponse {

    private final CommentResponse latestComment;
    private final TopicResponse topic;

    public static VoteResponse from(CommentResponse comment, TopicResponse topic) {
        return new VoteResponse(comment, topic);
    }
}
