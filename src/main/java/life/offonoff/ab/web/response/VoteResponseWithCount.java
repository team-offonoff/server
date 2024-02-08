package life.offonoff.ab.web.response;

import life.offonoff.ab.web.response.topic.TopicResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class VoteResponseWithCount extends VoteResponse {

    private List<ChoiceCountResponse> choiceCounts;

    public VoteResponseWithCount(CommentResponse latestComment, TopicResponse topic, List<ChoiceCountResponse> choiceCounts) {
        super(latestComment, topic);

        this.choiceCounts = choiceCounts;
    }

    public static VoteResponseWithCount from(CommentResponse latestComment, TopicResponse topic, List<ChoiceCountResponse> choiceCounts) {
        return new VoteResponseWithCount(latestComment, topic, choiceCounts);
    }
}
