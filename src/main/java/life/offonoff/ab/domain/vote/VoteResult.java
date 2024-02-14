package life.offonoff.ab.domain.vote;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.topic.Topic;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class VoteResult extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Topic topic;

    private int totalVoteCount;

    //== 연관관계 매핑 ==//
    public void setTopic(Topic topic) {
        this.topic = topic;
        topic.setVoteResult(this);
        this.totalVoteCount = topic.getVoteCount();
    }

    // Getter
    public Long getTopicId() {
        return topic.getId();
    }
}
