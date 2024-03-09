package life.offonoff.ab.web.response.topic.choice;

import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.web.response.topic.choice.content.ChoiceContentResponse;
import lombok.Getter;

@Getter
public class ChoiceResponse {

    private Long choiceId;
    private ChoiceContentResponse content;
    private ChoiceOption choiceOption;
    private int voteCount;

    public ChoiceResponse(Long choiceId, ChoiceContentResponse content, ChoiceOption choiceOption, int voteCount) {
        this.choiceId = choiceId;
        this.content = content;
        this.choiceOption = choiceOption;
        this.voteCount = voteCount;
    }

    public static ChoiceResponse from(Choice choice) {
        return new ChoiceResponse(
                choice.getId(),
                choice.generateContentResponse(),
                choice.getChoiceOption(),
                choice.getVoteCount());
    }
}
