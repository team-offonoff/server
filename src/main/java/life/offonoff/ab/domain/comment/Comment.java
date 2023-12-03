package life.offonoff.ab.domain.comment;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private int likeCount = 0;
    private int hateCount = 0;

    // Constructor
    public Comment(String content) {
        this.content = content;
    }

    //== 연관관계 매핑 ==//
    public void associate(Member member, Topic topic) {
        this.writer = member;
        member.addComment(this);

        this.topic = topic;
    }

    public void liked() {
        likeCount++;
    }

    public void hated() {
        hateCount++;
    }
}
