package life.offonoff.ab.domain.topic.hide;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import lombok.Getter;

@Getter
@Entity
public class HiddenTopic extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    //== Method ==//
    public void associate(Member member, Topic topic) {
        // topic이 먼저 등록돼야 함.
        this.topic = topic;
        this.member = member;

        if (member.hideTopic(this)) {
            topic.addHide(this);
        }
    }

    public boolean has(Topic topic) {
        return this.topic == topic;
    }
}
