package life.offonoff.ab.domain.topic.choice.content;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.topic.choice.Choice;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "content_form")
public abstract class ChoiceContent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "content", orphanRemoval = true)
    private Choice choice;

    public void setChoice(Choice choice) {
        this.choice = choice;
    }
}
