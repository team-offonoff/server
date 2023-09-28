package life.offonoff.ab.domain.topic;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.topic.choice.Choice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class TopicContent extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @OneToOne(mappedBy = "content", orphanRemoval = true)
    private Topic topic;

    /**
     * TopicContent - Choice 연관관계의 주인은 TopicContent로 설정
     * topiccontent -> choice 로의 참조만 필요하기에 검색 성능 향상
      */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "choice_a_id")
    private Choice choiceA;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "choice_b_id")
    private Choice choiceB;

    //== Constructor ==//
    public TopicContent(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void associate(Choice choiceA, Choice choiceB) {
        this.choiceA = choiceA;
        this.choiceB = choiceB;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
