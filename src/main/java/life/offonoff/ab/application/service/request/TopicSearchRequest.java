package life.offonoff.ab.application.service.request;

import life.offonoff.ab.domain.topic.TopicStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicSearchRequest {

    private TopicStatus topicStatus;
    private Long memberId;
    private Boolean hidden;
    private Long categoryId;

    @Builder
    public TopicSearchRequest(TopicStatus topicStatus, Long memberId, Boolean hidden, Long categoryId) {
        this.topicStatus = topicStatus;
        this.memberId = memberId;
        this.hidden = hidden;
        this.categoryId = categoryId;
    }
}
