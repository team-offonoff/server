package life.offonoff.ab.domain.topic;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.Member;
import life.offonoff.ab.domain.topic.choice.Choices;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "side")
public abstract class Topic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Embedded
    private Choices choices;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "publish_member_id")
    private Member member;

    @OneToMany(mappedBy = "topic")
    private List<TopicBlock> blocks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TopicStatus status = TopicStatus.VOTING;

    private int blockCount = 0;

    private LocalDateTime expiresAt;

    // Constructor
    public Topic(String title, String description, Choices choices) {
        this.title = title;
        this.description = description;
        this.choices = choices;
        this.expiresAt = LocalDateTime.now()
                                      .plusHours(24);
    }

    //== 연관관계 매핑 ==//
    public void associate(Member member) {
        this.member = member;
        member.publishTopic(this);
    }

    public void addTopicBlock(TopicBlock topicBlock) {
        this.blocks.add(topicBlock);
        blockCount++;
    }
}
