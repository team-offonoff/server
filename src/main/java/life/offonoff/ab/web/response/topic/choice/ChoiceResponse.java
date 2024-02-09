package life.offonoff.ab.web.response.topic.choice;

import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.web.response.topic.choice.content.ChoiceContentResponseFactory;
import life.offonoff.ab.web.response.topic.choice.content.ChoiceContentResponseFactory.ChoiceContentResponse;
import lombok.Getter;

@Getter
public class ChoiceResponse {

    private Long choiceId;
    private ChoiceContentResponse content;
    private ChoiceOption choiceOption;

    public ChoiceResponse(Long choiceId, ChoiceContentResponse content, ChoiceOption choiceOption) {
        this.choiceId = choiceId;
        this.content = content;
        this.choiceOption = choiceOption;
    }

    public static ChoiceResponse from(Choice choice) {
        return new ChoiceResponse(
                choice.getId(),
                ChoiceContentResponseFactory.create(choice.getContent()),
                choice.getChoiceOption());
    }
}
