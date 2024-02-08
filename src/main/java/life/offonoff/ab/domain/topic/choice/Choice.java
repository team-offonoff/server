package life.offonoff.ab.domain.topic.choice;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.topic.Topic;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Enumerated(EnumType.STRING)
    private ChoiceOption choiceOption;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "choice_content_id")
    private ChoiceContent content;

    //== Constructor ==//
    public Choice(Topic topic, ChoiceOption option, ChoiceContent content) {
        this.topic = topic;
        topic.addChoice(this);

        this.choiceOption = option;
        this.content = content;
    }

    //== Method ==//
}
