package life.offonoff.ab.domain.topic.choice;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ChoiceContent {

    private String text;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ChoiceContentType contentType;

    //== Method ==//
    public static ChoiceContent ofText(String text) {
        ChoiceContent content = new ChoiceContent();
        content.text = text;
        content.contentType = ChoiceContentType.TEXT;
        return content;
    }

    public static ChoiceContent ofImage(String imageUrl) {
        ChoiceContent content = new ChoiceContent();
        content.imageUrl = imageUrl;
        content.contentType = ChoiceContentType.IMAGE;
        return content;
    }

    public static ChoiceContent ofTextAndImage(String text, String imageUrl) {
        ChoiceContent content = new ChoiceContent();
        content.text = text;
        content.imageUrl = imageUrl;
        content.contentType = ChoiceContentType.TEXT_AND_IMAGE;
        return content;
    }
}
