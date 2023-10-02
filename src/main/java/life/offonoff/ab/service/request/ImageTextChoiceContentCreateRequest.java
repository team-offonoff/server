package life.offonoff.ab.service.request;

import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.topic.choice.content.ImageTextChoiceContent;

public class ImageTextChoiceContentCreateRequest implements ChoiceContentCreateRequest{
    private final String imageUrl;
    private final String text;

    public ImageTextChoiceContentCreateRequest(String imageUrl, String text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }

    @Override
    public ChoiceContent toEntity() {
        return new ImageTextChoiceContent(imageUrl, text);
    }
}
