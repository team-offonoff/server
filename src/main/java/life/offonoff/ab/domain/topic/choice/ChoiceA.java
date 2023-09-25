package life.offonoff.ab.domain.topic.choice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ChoiceA {
    @Column(name = "choice_a_img_url")
    private String imageUrl;
    @Column(name = "choice_a_text")
    private String text;

    //==Method==//
    public static ChoiceA ofImage(String imageUrl) {
        ChoiceA choiceA = new ChoiceA();
        choiceA.imageUrl = imageUrl;
        return choiceA;
    }

    public static ChoiceA ofText(String text) {
        ChoiceA choiceA = new ChoiceA();
        choiceA.text = text;
        return choiceA;
    }

    public static ChoiceA ofImageAndText(String imageUrl, String text) {
        ChoiceA choiceA = new ChoiceA();
        choiceA.imageUrl = imageUrl;
        choiceA.text = text;
        return choiceA;
    }
}
