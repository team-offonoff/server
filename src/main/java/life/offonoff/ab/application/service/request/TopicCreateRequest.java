package life.offonoff.ab.application.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import life.offonoff.ab.application.service.common.TextUtils;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.exception.LengthInvalidException;
import lombok.Builder;

import java.util.List;

@Builder
public record TopicCreateRequest(
        @NotNull(message = "토픽의 Side를 선택해주세요. (A/B)")
        TopicSide side,
        @NotBlank(message = "토픽의 키워드를 입력해주세요.")
        String keywordName,
        @NotBlank(message = "토픽의 title을 입력해주세요.")
        String title,
        List<ChoiceCreateRequest> choices,
        Long deadline
) {
    private static final int TOPIC_TITLE_MAX_LENGTH = 25;

    public TopicCreateRequest {
        if (TextUtils.countGraphemeClustersWithLongerEmoji(title) > TOPIC_TITLE_MAX_LENGTH) {
            throw new LengthInvalidException("토픽 제목", 1, TOPIC_TITLE_MAX_LENGTH);
        }
    }

}
