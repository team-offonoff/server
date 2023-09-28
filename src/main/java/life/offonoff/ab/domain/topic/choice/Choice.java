package life.offonoff.ab.domain.topic.choice;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Choice extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChoiceType type;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "choice_content_id")
    private ChoiceContent content;

    //== Constructor ==//
    public Choice(ChoiceType type) {
        this.type = type;
    }

    //== 연관관계 매핑 ==//
    public void associate(ChoiceContent contentA) {
        this.content = contentA;
        content.setChoice(this);
    }

    //== Method ==//
}
