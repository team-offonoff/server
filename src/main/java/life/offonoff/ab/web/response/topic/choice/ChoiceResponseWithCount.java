package life.offonoff.ab.web.response.topic.choice;

import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.web.response.topic.choice.content.ChoiceContentResponseFactory;
import lombok.Getter;

@Getter
public class ChoiceResponseWithCount extends ChoiceResponse {

    private int voteCount;

    public ChoiceResponseWithCount(Choice choice) {
        super(choice.getId(),
              ChoiceContentResponseFactory.create(choice.getContent()),
              choice.getChoiceOption());

        this.voteCount = choice.getVoteCount();
    }

    public static ChoiceResponseWithCount from(Choice choice) {
        return new ChoiceResponseWithCount(choice);
    }
}
