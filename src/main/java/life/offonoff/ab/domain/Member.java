package life.offonoff.ab.domain;

import jakarta.persistence.*;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicBlock;
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

    @OneToMany(mappedBy = "member")
    private List<Topic> publishedTopics = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<TopicBlock> topicBlocks = new ArrayList<>();

    //== Constructor ==//
    public Member(String name) {
        this.name = name;
    }

    //== 연관관계 매핑 ==//
    public void publishTopic(Topic topic) {
        this.publishedTopics.add(topic);
    }

    public void addTopicBlock(TopicBlock topicBlock) {

    }
}
