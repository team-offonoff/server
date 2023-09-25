package life.offonoff.ab.domain.topic;

import jakarta.persistence.*;
import life.offonoff.ab.domain.Member;

@Entity
public class TopicBlock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_ic")
    private Topic topic;
}
