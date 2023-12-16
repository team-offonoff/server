package life.offonoff.ab.domain.vote;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member voter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Enumerated(EnumType.STRING)
    private ChoiceOption selectedOption;

    private LocalDateTime votedAt;

    //== Constructor ==//
    public Vote(ChoiceOption choiceOption, LocalDateTime votedAt) {
        this.selectedOption = choiceOption;
        this.votedAt = votedAt;
    }

    //== Method ==//
    public void associate(Member voter, Topic topic) {
        this.voter = voter;
        voter.addVote(this);
        this.topic = topic;
        topic.addVote(this);
    }

    public void removeAssociations() {
        voter.getVotes().remove(this);
        topic.getVotes().remove(this);
    }

    public boolean isFor(Topic topic) {
        return this.topic.getId().equals(topic.getId());
    }
}
