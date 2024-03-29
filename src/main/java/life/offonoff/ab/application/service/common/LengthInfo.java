package life.offonoff.ab.application.service.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LengthInfo {
    COMMENT_CONTENT(1, 255),
    PAGEABLE_SIZE(0, 100),
    NICKNAME(1, 8),
    JOB_LENGTH(1, 12),
    TOPIC_TITLE(1, 20),
    TOPIC_CHOICE_TEXT(1, 25),
    TOPIC_KEYWORD(1, 6)
    ;

    private final int minLength;
    private final int maxLength;
}
