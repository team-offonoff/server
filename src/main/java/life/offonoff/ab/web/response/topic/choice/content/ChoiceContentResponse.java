package life.offonoff.ab.web.response.topic.choice.content;

import life.offonoff.ab.domain.topic.choice.content.ChoiceContentType;

public interface ChoiceContentResponse {
    record ImageTextChoiceContentResponse(
            String imageUrl,
            String text,
            String type
    ) implements ChoiceContentResponse {
        public ImageTextChoiceContentResponse(String imageUrl, String text) {
            this(imageUrl, text, ChoiceContentType.IMAGE_WITH_TEXT_CHOICE_CONTENT);
        }
    }
}
