package life.offonoff.ab.domain.topic.choice.content;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("IMAGE")
public class ImageChoiceContent extends ChoiceContent {

    private String imageUrl;
}

