package life.offonoff.ab.domain.comment;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.IllegalAuthorException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Enumerated(EnumType.STRING)
    private ChoiceOption writersVotedOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Topic topic;

    private int likeCount = 0;
    private int hateCount = 0;

    // Constructor
    public Comment(String content) {
        this.content = content;
    }

    private Comment(Member member, Topic topic, String content) {
        associate(member, topic);
        this.content = content;
    }

    public Comment(Member member, Topic topic, ChoiceOption selectedOption, String content) {
        associate(member, topic);
        this.writersVotedOption = selectedOption;
        this.content = content;
    }


    private void associate(Member member, Topic topic) {
        this.writer = member;
        member.addComment(this);

        this.topic = topic;
        topic.commented();
    }

    public static Comment createAuthorsComment(Member author, Topic topic, String content) {
        if (!topic.isWrittenBy(author)) {
            throw new IllegalAuthorException(author.getId(), topic.getId());
        }

        return new Comment(author, topic, content);
    }

    public static Comment createVotersComment(Vote vote, String content) {
        return new Comment(vote.getVoter(),
                           vote.getTopic(),
                           vote.getSelectedOption(),
                           content);
    }

    public void increaseLikeCount() {
        likeCount++;
    }

    public void decreaseLikeCount() {
        likeCount--;
    }

    public void increaseHateCount() {
        hateCount++;
    }

    public void decreaseHateCount() {
        hateCount--;
    }
    public boolean isWrittenBy(Member member) {
        return this.writer == member;
    }

    public void remove() {
        topic.commentRemoved();
        writer.removeComment(this);
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
