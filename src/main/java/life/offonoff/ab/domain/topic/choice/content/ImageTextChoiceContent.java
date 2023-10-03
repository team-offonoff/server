package life.offonoff.ab.domain.topic.choice.content;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue(ChoiceContentType.IMAGE_WITH_TEXT_CHOICE_CONTENT)
public class ImageTextChoiceContent extends ChoiceContent {
    private String imageUrl;
    private String text;

    public ImageTextChoiceContent(String imageUrl, String text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }
}
