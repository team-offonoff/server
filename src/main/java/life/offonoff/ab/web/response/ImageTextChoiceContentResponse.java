package life.offonoff.ab.web.response;

import life.offonoff.ab.domain.topic.choice.content.ChoiceContentType;
import life.offonoff.ab.web.response.ChoiceContentResponseFactory.ChoiceContentResponse;

public record ImageTextChoiceContentResponse(
        String text,
        String imageUrl,
        String type
) implements ChoiceContentResponse {
    public ImageTextChoiceContentResponse(String text, String imageUrl) {
        this(text, imageUrl, ChoiceContentType.IMAGE_WITH_TEXT_CHOICE_CONTENT);
    }
}