package life.offonoff.ab.domain.topic.choice.content;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.topic.choice.Choice;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "content_kind")
public abstract class ChoiceContent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "content", orphanRemoval = true)
    private Choice choice;

    @Transient
    private ChoiceContentKind kind;

    //== Constructor ==//
    public ChoiceContent(ChoiceContentKind kind) {
        this.kind = kind;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }
}
