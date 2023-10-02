package life.offonoff.ab.service.request;

import life.offonoff.ab.domain.topic.choice.ChoiceOption;

public record ChoiceCreateRequest (
        ChoiceOption choiceOption,
        ChoiceContentCreateRequest choiceContentRequest
){
}
