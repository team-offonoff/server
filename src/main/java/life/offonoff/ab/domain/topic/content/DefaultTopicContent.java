package life.offonoff.ab.domain.topic.content;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DEFAULT")
public class DefaultTopicContent extends TopicContent {

    public DefaultTopicContent() {
        super(TopicContentKind.DEFAULT);
    }
}
