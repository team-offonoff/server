package life.offonoff.ab.web.response.topic.choice;

import life.offonoff.ab.domain.topic.choice.Choice;
import lombok.Getter;

@Getter
public class ChoiceResponseWithCount extends ChoiceResponse {

    private final int voteCount;

    public ChoiceResponseWithCount(Choice choice) {
        super(choice.getId(),
              choice.generateContentResponse(),
              choice.getChoiceOption());

        this.voteCount = choice.getVoteCount();
    }

    public static ChoiceResponseWithCount from(Choice choice) {
        return new ChoiceResponseWithCount(choice);
    }
}
