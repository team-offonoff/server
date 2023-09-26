package life.offonoff.ab.domain.topic.block;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;

@Entity
public class TopicBlock extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private int active = 1;

    //== Method ==//
    public void associate(Member member, Topic topic) {
        this.member = member;
        member.addTopicBlock(this);
        this.topic = topic;
        topic.addTopicBlock(this);
    }

    public void cancel() {
        this.active = 0;
    }
}
