package life.offonoff.ab.domain.topic;

import jakarta.persistence.*;
import life.offonoff.ab.domain.topic.choice.Choice;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class TopicContent {

    private String title;
    private String description;

    @Embedded
    @AttributeOverride(name = "content.text", column = @Column(name = "choice_a_text"))
    @AttributeOverride(name = "content.imageUrl", column = @Column(name = "choice_a_image_url"))
    @AttributeOverride(name = "content.contentType", column = @Column(name = "choice_a_content_type"))
    private Choice choiceA;

    @Embedded
    @AttributeOverride(name = "content.text", column = @Column(name = "choice_b_text"))
    @AttributeOverride(name = "content.imageUrl", column = @Column(name = "choice_b_image_url"))
    @AttributeOverride(name = "content.contentType", column = @Column(name = "choice_b_content_type"))
    private Choice choiceB;
}
