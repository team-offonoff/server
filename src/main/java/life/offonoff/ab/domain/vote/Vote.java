package life.offonoff.ab.domain.vote;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.choice.ChoiceSide;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Enumerated(EnumType.STRING)
    private ChoiceSide choiceSide;

    private int active = 1;

    //== Constructor ==//
    public Vote(ChoiceSide choiceSide) {
        this.choiceSide = choiceSide;
    }

    //== Method ==//
    public void associate(Member member, Topic topic) {
        this.member = member;
        member.addVote(this);
        this.topic = topic;
        topic.addVote(this);
    }

    public void cancel() {
        this.active = 0;
    }
}
