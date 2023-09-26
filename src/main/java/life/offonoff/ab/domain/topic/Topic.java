package life.offonoff.ab.domain.topic;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.topic.block.TopicBlock;
import life.offonoff.ab.domain.vote.Vote;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Topic extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @Embedded
    private TopicContent content;

    @Enumerated(EnumType.STRING)
    private TopicSide side;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "publish_member_id")
    private Member publishMember;

    @OneToMany(mappedBy = "topic")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL)
    private List<TopicBlock> blocks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TopicStatus status = TopicStatus.VOTING;

    private int commentCount = 0;
    private int voteCount = 0;
    private int blockCount = 0;
    private LocalDateTime expiresAt;
    private int active = 1;

    // Constructor
    public Topic(TopicSide side, TopicContent content) {
        this.side = side;
        this.content = content;
        this.expiresAt = LocalDateTime.now()
                                      .plusHours(24);
    }

    //== 연관관계 매핑 ==//
    public void associate(Member member, Category category) {
        this.publishMember = member;
        member.publishTopic(this);

        this.category = category;
        category.addTopic(this);
    }

    public void addTopicBlock(TopicBlock topicBlock) {
        this.blocks.add(topicBlock);
        blockCount++;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        commentCount++;
    }

    public void addVote(Vote vote) {
        votes.add(vote);
        voteCount++;
    }

    public void remove() {
        this.active = 0;
    }
}
