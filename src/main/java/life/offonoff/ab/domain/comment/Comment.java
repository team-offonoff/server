package life.offonoff.ab.domain.comment;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private int active = 1;

    // Constructor
    public Comment(String content) {
        this.content = content;
    }

    public void associate(Member member, Topic topic) {
        this.member = member;
        member.addComment(this);

        this.topic = topic;
        topic.addComment(this);
    }

    public void cancel() {
        this.active = 0;
    }
}
