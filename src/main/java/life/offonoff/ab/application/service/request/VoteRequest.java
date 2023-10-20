package life.offonoff.ab.application.service.request;

import life.offonoff.ab.domain.topic.choice.ChoiceOption;

import java.time.LocalDateTime;

public record VoteRequest(
        Long topicId,
        ChoiceOption choiceOption,
        LocalDateTime requestTime
) {
}
