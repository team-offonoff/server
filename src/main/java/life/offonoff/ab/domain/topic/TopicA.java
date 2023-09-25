package life.offonoff.ab.domain.topic;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import life.offonoff.ab.domain.topic.choice.Choices;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("TOPIC_A")
public class TopicA extends Topic {

    @Builder
    public TopicA(String title, String description, Choices choices) {
        super(title, description, choices);
    }
}
