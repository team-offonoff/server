package life.offonoff.ab.web.response;

import lombok.Getter;

import java.util.List;

@Getter
public class VoteResponseWithCount extends VoteResponse {

    private List<ChoiceCountResponse> choiceCounts;

    public VoteResponseWithCount(List<ChoiceCountResponse> choiceCounts, CommentResponse latestComment) {
        super(latestComment);

        this.choiceCounts = choiceCounts;
    }

    public static VoteResponseWithCount from(List<ChoiceCountResponse> choiceCounts, CommentResponse latestComment) {
        return new VoteResponseWithCount(choiceCounts, latestComment);
    }
}
