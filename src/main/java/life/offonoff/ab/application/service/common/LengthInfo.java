package life.offonoff.ab.application.service.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LengthInfo {
    COMMENT_CONTENT(1, 255),
    PAGEABLE_SIZE(0, 100),
    NICKNAME_LENGTH(1, 8)
    ;

    private final int minLength;
    private final int maxLength;
}
