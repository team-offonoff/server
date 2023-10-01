package life.offonoff.ab.service.dto;

import life.offonoff.ab.domain.topic.TopicStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopicSearchParams {

    private TopicStatus topicStatus;
    private Long memberId;
    private Long categoryId;
}
