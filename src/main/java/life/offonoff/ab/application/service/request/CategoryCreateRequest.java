package life.offonoff.ab.application.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import life.offonoff.ab.domain.topic.TopicSide;

public record CategoryCreateRequest(
        @NotBlank(message = "카테고리의 이름을 입력해주세요.")
        String name,
        @NotNull(message = "카테고리의 TopicSide를 입력해주세요. (TOPIC_A/TOPIC_B)")
        TopicSide topicSide
) {
}
