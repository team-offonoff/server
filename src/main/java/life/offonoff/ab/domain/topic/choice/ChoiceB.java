package life.offonoff.ab.domain.topic.choice;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ChoiceB {
    @Column(name = "choice_b_img_url")
    private String imageUrl;
    @Column(name = "choice_b_text")
    private String text;

    //==Method==//
    public static ChoiceB ofImage(String imageUrl) {
        ChoiceB choiceB = new ChoiceB();
        choiceB.imageUrl = imageUrl;
        return choiceB;
    }

    public static ChoiceB ofText(String text) {
        ChoiceB choiceB = new ChoiceB();
        choiceB.text = text;
        return choiceB;
    }

    public static ChoiceB ofImageAndText(String imageUrl, String text) {
        ChoiceB choiceB = new ChoiceB();
        choiceB.imageUrl = imageUrl;
        choiceB.text = text;
        return choiceB;
    }
}
