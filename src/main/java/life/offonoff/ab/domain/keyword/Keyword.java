package life.offonoff.ab.domain.keyword;

import jakarta.persistence.*;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueNameInSide", columnNames = { "name", "side" })
})
public class Keyword {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String name;
    @Enumerated(EnumType.STRING)
    private TopicSide side;

    @OneToMany(mappedBy = "keyword")
    private List<Topic> topics = new ArrayList<>();

    //== Constructor ==//
    public Keyword(String name, TopicSide side) {
        this.name = name;
        this.side = side;
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
    }

    @Override
    public boolean equals(Object obj) {
        return id.equals(((Keyword) obj).getId());
    }
}
