package life.offonoff.ab.domain.topic;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("TOPIC_A")
public class TopicA extends Topic {

    public TopicA(String title) {
        super(title);
    }
}
