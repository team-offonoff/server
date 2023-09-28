package life.offonoff.ab.domain.topic.choice.content;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("IMAGE_WITH_TEXT")
public class ImageWithTextChoiceContent extends ChoiceContent {

    private String imageUrl;
    private String text;

    public ImageWithTextChoiceContent(String imageUrl, String text) {
        this.imageUrl = imageUrl;
        this.text = text;
    }
}
