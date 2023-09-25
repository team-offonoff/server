package life.offonoff.ab.domain.topic;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import life.offonoff.ab.domain.topic.choice.Choices;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Entity
@DiscriminatorValue("TOPIC_B")
public class TopicB extends Topic {

    @Builder
    public TopicB(String title, String description, Choices choices) {
        super(title, description, choices);
    }
}
