package life.offonoff.ab.service.request;

import life.offonoff.ab.domain.topic.TopicStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicSearchRequest {

    private TopicStatus topicStatus;
    private Long memberId;
    private Boolean hidden;
    private Long categoryId;
}
