package life.offonoff.ab.application.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import life.offonoff.ab.application.service.common.TextUtils;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.exception.LengthInvalidException;
import lombok.Builder;

import java.util.List;

import static life.offonoff.ab.application.service.common.LengthInfo.TOPIC_KEYWORD;
import static life.offonoff.ab.application.service.common.LengthInfo.TOPIC_TITLE;

@Builder
public record TopicCreateRequest(
        @NotNull(message = "토픽의 Side를 선택해주세요. (A/B)")
        TopicSide side,
        String keywordName,
        @NotBlank(message = "토픽의 title을 입력해주세요.")
        String title,
        List<ChoiceCreateRequest> choices,
        Long deadline
) {
    public TopicCreateRequest {
        int titleLength = TextUtils.countGraphemeClusters(title);
        if (titleLength < TOPIC_TITLE.getMinLength() || titleLength > TOPIC_TITLE.getMaxLength()) {
            throw new LengthInvalidException("토픽 제목", TOPIC_TITLE);
        }
        int keywordLength = TextUtils.countGraphemeClusters(keywordName);
        if (keywordLength < TOPIC_KEYWORD.getMinLength() || keywordLength > TOPIC_KEYWORD.getMaxLength()) {
            throw new LengthInvalidException("토픽 키워드", TOPIC_KEYWORD);
        }
    }

}
