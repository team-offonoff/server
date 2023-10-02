package life.offonoff.ab.service.request;

import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.topic.choice.content.ImageTextChoiceContent;

public record ImageTextChoiceContentCreateRequest(
        String imageUrl,
        String text
) implements ChoiceContentCreateRequest{
    @Override
    public ChoiceContent toEntity() {
        return new ImageTextChoiceContent(imageUrl, text);
    }
}
