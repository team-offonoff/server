package life.offonoff.ab.application.service.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import life.offonoff.ab.application.service.common.TextUtils;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContentType;
import life.offonoff.ab.domain.topic.choice.content.ImageTextChoiceContent;
import life.offonoff.ab.exception.LengthInvalidException;
import life.offonoff.ab.exception.NotKoreanEnglishNumberException;

import static life.offonoff.ab.application.service.common.LengthInfo.TOPIC_CHOICE_TEXT;

@JsonTypeName(ChoiceContentType.IMAGE_WITH_TEXT_CHOICE_CONTENT)
public record ImageTextChoiceContentCreateRequest(
        String imageUrl,
        String text
) implements ChoiceContentCreateRequest{
    @Override
    public ChoiceContent toEntity() {
        return new ImageTextChoiceContent(imageUrl, text);
    }

    public ImageTextChoiceContentCreateRequest {
        int length = TextUtils.countGraphemeClusters(text);
        if (length < TOPIC_CHOICE_TEXT.getMinLength() || length > TOPIC_CHOICE_TEXT.getMaxLength()) {
            throw new LengthInvalidException("토픽 선택지 텍스트", TOPIC_CHOICE_TEXT);
        }
        if (!TextUtils.isOnlyKoreanEnglishNumberIncluded(text)) {
            throw new NotKoreanEnglishNumberException(text);
        }
    }
}
