package life.offonoff.ab.service.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContentType;
import life.offonoff.ab.domain.topic.choice.content.ImageTextChoiceContent;

@JsonTypeName(ChoiceContentType.IMAGE_WITH_TEXT_CHOICE_CONTENT)
public record ImageTextChoiceContentCreateRequest(
        String imageUrl,
        String text
) implements ChoiceContentCreateRequest{
    @Override
    public ChoiceContent toEntity() {
        return new ImageTextChoiceContent(imageUrl, text);
    }
}
