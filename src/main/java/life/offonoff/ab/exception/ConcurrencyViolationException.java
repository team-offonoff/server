package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public abstract class ConcurrencyViolationException extends AbException{
    private static final AbCode AB_CODE = AbCode.CONCURRENCY_VIOLATION;
    private static final String MESSAGE = "서버 문제가 발생했습니다. 다시 시도해주세요.";

    public ConcurrencyViolationException() {
        super(MESSAGE);
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.CONFLICT.value();
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
