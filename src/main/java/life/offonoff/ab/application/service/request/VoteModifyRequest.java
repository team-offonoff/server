package life.offonoff.ab.application.service.request;

import jakarta.validation.constraints.NotNull;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VoteModifyRequest {

    @NotNull
    private ChoiceOption modifiedOption;
    @NotNull
    private Long modifiedAt;
}
