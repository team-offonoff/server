package life.offonoff.ab.application.service.request;

import jakarta.validation.constraints.NotNull;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;

import java.time.LocalDateTime;

public record VoteRequest(
        @NotNull Long memberId,
        @NotNull ChoiceOption choiceOption,
        @NotNull LocalDateTime requestTime,
        Boolean vote
) {
}
