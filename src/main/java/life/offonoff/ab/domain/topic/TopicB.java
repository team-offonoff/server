package life.offonoff.ab.domain.topic;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Entity
@DiscriminatorValue("TOPIC_B")
public class TopicB extends Topic {

    public TopicB(String title) {
        super(title);
    }
}
