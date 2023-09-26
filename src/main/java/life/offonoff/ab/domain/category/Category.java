package life.offonoff.ab.domain.category;

import jakarta.persistence.*;
import life.offonoff.ab.domain.topic.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category")
    private List<Topic> topics = new ArrayList<>();

    //== Constructor ==//
    public Category(String name) {
        this.name = name;
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
    }
}
