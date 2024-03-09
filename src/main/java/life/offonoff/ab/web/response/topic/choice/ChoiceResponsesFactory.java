package life.offonoff.ab.web.response.topic.choice;

import life.offonoff.ab.domain.topic.Topic;

import java.util.List;

public class ChoiceResponsesFactory {

    public static List<? extends ChoiceResponse> create(Topic topic) {

        return topic.getChoices()
                    .stream()
                    .map(ChoiceResponse::from)
                    .toList();
    }
}
