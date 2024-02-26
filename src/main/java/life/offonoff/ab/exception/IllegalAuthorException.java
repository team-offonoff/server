package life.offonoff.ab.exception;

import life.offonoff.ab.exception.AbCode;
import life.offonoff.ab.exception.IllegalArgumentException;

public class IllegalAuthorException extends IllegalArgumentException {

    private static final String MESSAGE = "토픽 작성자가 아닙니다.";
    private final Long memberId;
    private final long topicId;

    public IllegalAuthorException(Long memberId, Long topicId) {
        super(MESSAGE);
        this.memberId = memberId;
        this.topicId = topicId;
    }

    @Override
    public String getHint() {
        return "회원[id : " + memberId + "]은 토픽[id : " + topicId + "]의 작성자가 아닙니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.ILLEGAL_AUTHOR;
    }
}
