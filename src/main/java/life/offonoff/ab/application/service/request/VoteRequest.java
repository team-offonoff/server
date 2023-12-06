package life.offonoff.ab.application.service.request;

import jakarta.validation.constraints.NotNull;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;

public record VoteRequest(
        @NotNull ChoiceOption choiceOption,
        @NotNull Long votedAt
) {
}
