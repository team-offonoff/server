package life.offonoff.ab.domain.topic.content;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.topic.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "content_kind")
public abstract class TopicContent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "content", orphanRemoval = true)
    private Topic topic;

    @Transient
    private TopicContentKind kind;

    //== Constructor ==//
    public TopicContent(TopicContentKind kind) {
        this.kind = kind;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
