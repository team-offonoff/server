package life.offonoff.ab.domain.topic.content;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "content_type")
public abstract class TopicContent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private TopicContentType type;

    //== Constructor ==//
    public TopicContent(TopicContentType type) {
        this.type = type;
    }
}
