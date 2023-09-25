package life.offonoff.ab.domain.topic.choice;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Choices {

    @Embedded
    private ChoiceA choiceA;
    @Embedded
    private ChoiceB choiceB;

    public Choices(ChoiceA choiceA, ChoiceB choiceB) {
        this.choiceA = choiceA;
        this.choiceB = choiceB;
    }
}
