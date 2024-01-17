package life.offonoff.ab.application.service.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LengthInfo {
    // TODO: 댓글 최대 길이 요구사항대로 수정
    COMMENT_CONTENT(1, 100),

    PAGEABLE_SIZE(0, 100)
    ;

    private final int minLength;
    private final int maxLength;
}
