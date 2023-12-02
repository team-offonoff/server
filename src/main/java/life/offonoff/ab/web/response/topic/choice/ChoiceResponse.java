package life.offonoff.ab.web.response.topic.choice;

import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.web.response.topic.choice.content.ChoiceContentResponseFactory;
import life.offonoff.ab.web.response.topic.choice.content.ChoiceContentResponseFactory.ChoiceContentResponse;

public record ChoiceResponse(
        Long choiceId,
        ChoiceContentResponse content,
        ChoiceOption choiceOption
) {
    public static ChoiceResponse from(Choice choice) {
        return new ChoiceResponse(
                choice.getId(),
                ChoiceContentResponseFactory.create(choice.getContent()),
                choice.getChoiceOption());
    }
}