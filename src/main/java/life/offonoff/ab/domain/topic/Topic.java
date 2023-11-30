package life.offonoff.ab.domain.topic;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.content.TopicContent;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.domain.vote.VotingResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Topic extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_content_id")
    private TopicContent content;

    @OneToMany(mappedBy = "topic")
    private List<Choice> choices = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TopicSide side;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private Member author;

    @OneToMany(mappedBy = "topic")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Vote> votes = new ArrayList<>();

    // 운영 측면에서 hide 정보 추적
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HiddenTopic> hides = new ArrayList<>();

    @OneToOne(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private VotingResult votingResult;

    @Enumerated(EnumType.STRING)
    private TopicStatus status = TopicStatus.VOTING;

    private int commentCount = 0;
    private int voteCount = 0;
    private int hideCount = 0;
    private LocalDateTime deadline;
    private boolean active = true;

    // Constructor
    public Topic(String title, TopicSide side, LocalDateTime deadline) {
        this.title = title;
        this.side = side;
        this.deadline = deadline;
    }

    public Topic(String title, TopicSide side) {
        this(title, side, LocalDateTime.now().plusHours(24));
    }

    public Topic(Member member, Keyword keyword, String title, TopicSide side) {
        this(member, keyword, title, side, LocalDateTime.now().plusHours(24));
    }

    public Topic(Member member, Keyword keyword, String title, TopicSide side, LocalDateTime deadline) {
        this.title = title;
        this.side = side;
        this.deadline = deadline;
        associate(member, keyword, null);
    }

    //== 연관관계 매핑 ==//
    public void associate(Member member, Keyword keyword, TopicContent content) {
        this.author = member;
        member.publishTopic(this);

        this.keyword = keyword;
        keyword.addTopic(this);

        this.content = content;
    }

    //== Getter ==//
    public Long getDeadlineSecond() {
        return deadline.atZone(ZoneId.systemDefault()).toEpochSecond();
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

    public void activate(boolean active) {
        this.active = active;
    }

    public void addChoice(Choice choice) {
        this.choices.add(choice);
    }

    public void removeHiddenBy(Member member) {
        this.hides.removeIf(h -> h.has(member));
        member.cancelHide(this);
        hideCount--;
    }

    public void setVotingResult(VotingResult votingResult) {
        this.votingResult = votingResult;
    }

    public boolean votable(LocalDateTime requestTime) {
        return requestTime.isBefore(deadline);
    }

    public void endVote() {
        this.status = TopicStatus.VOTING_ENDED;
    }

    public void noticed() {
        this.status = TopicStatus.NOTICED;
    }
}
