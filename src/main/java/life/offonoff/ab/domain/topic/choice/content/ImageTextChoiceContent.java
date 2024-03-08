package life.offonoff.ab.domain.topic.choice.content;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import life.offonoff.ab.web.response.topic.choice.content.ChoiceContentResponse;
import life.offonoff.ab.web.response.topic.choice.content.ChoiceContentResponse.ImageTextChoiceContentResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(ChoiceContentType.IMAGE_WITH_TEXT_CHOICE_CONTENT)
public class ImageTextChoiceContent extends ChoiceContent {
    private String imageUrl;
    @Column(length = 255)
    private String text;

    public ImageTextChoiceContent(String imageUrl, String text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }

    @Override
    public ChoiceContentResponse toResponse() {
        return new ImageTextChoiceContentResponse(imageUrl, text);
    }
}
