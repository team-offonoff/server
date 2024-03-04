package life.offonoff.ab.domain.topic.choice;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.web.response.topic.choice.content.ChoiceContentResponse;
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

    private int voteCount;

    //== Constructor ==//
    public Choice(Topic topic, ChoiceOption option, ChoiceContent content) {
        this.topic = topic;
        topic.addChoice(this);

        this.choiceOption = option;
        this.content = content;
    }

    //== Method ==//
    public boolean isOptionOf(ChoiceOption selectedOption) {
        return this.choiceOption.equals(selectedOption);
    }

    public void increaseVoteCount() {
        this.voteCount++;
    }

    public void decreaseVoteCount() {
        this.voteCount--;
    }

    public ChoiceContentResponse generateContentResponse() {
        if (content == null) {
            return null;
        }
        return content.toResponse();
    }
}
