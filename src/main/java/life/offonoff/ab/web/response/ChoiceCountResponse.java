package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChoiceCountResponse {

    private ChoiceOption choiceOption;
    private Long voteCount;
}
