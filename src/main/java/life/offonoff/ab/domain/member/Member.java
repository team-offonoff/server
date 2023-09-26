package life.offonoff.ab.domain.member;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.block.TopicBlock;
import life.offonoff.ab.domain.vote.Vote;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String nickname;
    @Embedded
    private AlarmEnables alarmEnables;

    @OneToMany(mappedBy = "publishMember")
    private List<Topic> publishedTopics = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TopicBlock> topicBlocks = new ArrayList<>();

    private int active = 1;

    //== Constructor ==//
    public Member(String name, String nickname, AlarmEnables alarmEnables) {
        this.name = name;
        this.nickname = nickname;
        this.alarmEnables = alarmEnables;
    }

    //== 연관관계 매핑 ==//
    public void publishTopic(Topic topic) {
        publishedTopics.add(topic);
    }

    public void addTopicBlock(TopicBlock topicBlock) {
        topicBlocks.add(topicBlock);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addVote(Vote vote) {
        votes.add(vote);
    }

    public void inactive() {
        this.active = 0;
    }
}
