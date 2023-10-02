package life.offonoff.ab.domain.topic;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.content.TopicContent;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.vote.Vote;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Topic extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_content_id")
    private TopicContent content;

    @OneToMany(mappedBy = "topic")
    private List<Choice> choices = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TopicSide side;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "publish_member_id")
    private Member publishMember;

    @OneToMany(mappedBy = "topic")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Vote> votes = new ArrayList<>();

    // 운영 측면에서 hide 정보 추적
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HiddenTopic> hides = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TopicStatus status = TopicStatus.VOTING;

    private int commentCount = 0;
    private int voteCount = 0;
    private int hideCount = 0;
    private LocalDateTime deadline;
    private int active = 1;

    // Constructor
    public Topic(String title, TopicSide side, LocalDateTime deadline) {
        this.title = title;
        this.side = side;
        this.deadline = deadline;
    }

    public Topic(String title, TopicSide side) {
        this(title, side, LocalDateTime.now().plusHours(24));
    }

    //== 연관관계 매핑 ==//
    public void associate(Member member, Category category, TopicContent content) {
        this.publishMember = member;
        member.publishTopic(this);

        this.category = category;
        category.addTopic(this);

        this.content = content;
    }

    public void addHide(HiddenTopic hiddenTopic) {
        this.hides.add(hiddenTopic);
        hideCount++;
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

    public void addChoice(Choice choice) {
        this.choices.add(choice);
    }

  public void removeHiddenBy(Member member) {
        this.hides.removeIf(h -> h.has(member));
        member.cancelHide(this);
        hideCount--;
    }
}
