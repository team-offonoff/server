package life.offonoff.ab.domain.topic;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.topic.content.TopicContent;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.domain.vote.VoteResult;
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

    @Column(length = 40)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_content_id")
    private TopicContent content;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private List<Choice> choices = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TopicSide side;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "author_id")
    private Member author;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    // 운영 측면에서 hide 정보 추적
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HiddenTopic> hides = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TopicReport> reports = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "voting_result_id")
    private VoteResult voteResult;

    @Enumerated(EnumType.STRING)
    private TopicStatus status = TopicStatus.VOTING;

    private int commentCount = 0;
    private int voteCount = 0;
    private int hideCount = 0;
    private LocalDateTime deadline;
    private boolean active = true;

    // Constructor
    public Topic(Member member, Keyword keyword, String title, TopicSide side, LocalDateTime deadline) {
        this.title = title;
        this.side = side;
        this.deadline = deadline;
        associate(member, keyword, null);
    }

    public Topic(Member member, String title, TopicSide side) {
        this(member, null, title, side, null);
    }

    //== 연관관계 매핑 ==//
    public void associate(Member member, Keyword keyword, TopicContent content) {
        this.author = member;
        member.publishTopic(this);

        if (keyword != null) {
            this.keyword = keyword;
            keyword.addTopic(this);
        }

        this.content = content;
    }

    //== Getter ==//
    public Long getDeadlineSecond() {
        if (deadline == null){
            return null;
        }
        return deadline.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public void addHide(HiddenTopic hiddenTopic) {
        this.hides.add(hiddenTopic);
        hideCount++;
    }

    public void addVote(Vote vote) {
        votes.add(vote);
        voteCount++;

        Choice selectedChoice = getChoiceByOption(vote.getSelectedOption());
        selectedChoice.increaseVoteCount();
    }

    private Choice getChoiceByOption(ChoiceOption option) {
        return this.choices.stream()
                           .filter(choice -> choice.isOptionOf(option))
                           .findFirst()
                           .get();
    }

    public void changeVotedChoiceOption(ChoiceOption beforeOption, ChoiceOption afterOption) {
        for (Choice choice : choices) {
            if (choice.isOptionOf(beforeOption)) {
                choice.decreaseVoteCount();
            }

            if (choice.isOptionOf(afterOption)) {
                choice.increaseVoteCount();
            }
        }
    }

    public void activate(boolean active) {
        this.active = active;
    }

    public void addChoice(Choice choice) {
        this.choices.add(choice);
    }

    public void setVoteResult(VoteResult voteResult) {
        this.voteResult = voteResult;
    }

    public boolean isBeforeDeadline(LocalDateTime requestTime) {
        if (deadline == null) {
            return true;
        }
        return requestTime.isBefore(deadline);
    }

    public void closeVote() {
        this.status = TopicStatus.CLOSED;
    }

    public void noticed() {
        this.status = TopicStatus.NOTICED;
    }

    public boolean isReportedBy(Member member) {
        return reports.stream()
                .anyMatch(report -> report.getMember().getId().equals(member.getId()));
    }

    public void reportBy(Member member) {
        reports.add(new TopicReport(member, this));
    }

    public void commented() {
        this.commentCount++;
    }

    public void commentRemoved(int amount) {
        this.commentCount -= amount;
    }

    public void commentRemoved() {
        commentRemoved(1);
    }

    public boolean isWrittenBy(Member member) {
        return this.author.getId().equals(member.getId());
    }

    public boolean shouldHaveVoteResult() {
        return side.equals(TopicSide.TOPIC_A);
    }

    public boolean shouldHaveLatestComment() {
        return side.equals(TopicSide.TOPIC_B);
    }
}
