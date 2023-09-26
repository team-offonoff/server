package life.offonoff.ab.domain.topic.choice;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static life.offonoff.ab.domain.topic.choice.ChoiceSide.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Choice {

    @Transient
    private ChoiceSide side;

    @Embedded
    private ChoiceContent content;

    //== Constructor ==//
    private Choice(ChoiceSide side, ChoiceContent content) {
        this.side = side;
        this.content = content;
    }

    //== Method ==//
    public static Choice createChoiceA(ChoiceContent content) {
        return new Choice(CHOICE_A, content);
    }

    public static Choice createChoiceB(ChoiceContent content) {
        return new Choice(CHOICE_B, content);
    }
}
