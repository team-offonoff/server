package life.offonoff.ab.domain.vote;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.notification.Notification;
import life.offonoff.ab.domain.notification.VoteResultNotification;
import life.offonoff.ab.domain.topic.Topic;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class VoteResult extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "topic_id")
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
