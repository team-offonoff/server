package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.topic.choice.content.ImageTextChoiceContent;

public class ChoiceContentResponseFactory {
    public static ChoiceContentResponse create(ChoiceContent content) {
        if (content instanceof ImageTextChoiceContent) {
            return new ImageTextChoiceContentResponse(
                    ((ImageTextChoiceContent) content).getText(),
                    ((ImageTextChoiceContent) content).getImageUrl());
        }
        return null;
    }

    public interface ChoiceContentResponse {
    }
}
