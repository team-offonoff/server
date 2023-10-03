package life.offonoff.ab.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.exception.LengthInvalidException;
import life.offonoff.ab.service.common.TextUtils;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TopicCreateRequest(
        @NotNull(message = "Topic Side를 선택해주세요. (A/B)")
        TopicSide topicSide,
        // TODO: 분야를 선택안한 토픽도 가능한가?
        Long categoryId,
        @NotBlank(message = "Topic title을 입력해주세요.")
        String topicTitle,
        List<ChoiceCreateRequest> choices,
        LocalDateTime deadline
) {
    private static final int TOPIC_TITLE_MAX_LENGTH = 25;

    public TopicCreateRequest {
        if (TextUtils.getLengthOfEmojiContainableText(topicTitle) > TOPIC_TITLE_MAX_LENGTH) {
            throw new LengthInvalidException("토픽 제목", 1, TOPIC_TITLE_MAX_LENGTH);
        }
    }

}
