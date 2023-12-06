package life.offonoff.ab.exception;

import java.time.LocalDateTime;

public class FutureTimeRequestException extends IllegalArgumentException {
    private static final String MESSAGE = "미래 시간으로 요청은 불가능합니다.";
    private static final AbCode AB_CODE = AbCode.FUTURE_TIME_REQUEST;
    private final LocalDateTime requestTime;
    private final LocalDateTime standardTime;
    public FutureTimeRequestException(LocalDateTime requestTime, LocalDateTime standardTime) {
        super(MESSAGE);
        this.requestTime = requestTime;
        this.standardTime = standardTime;
    }

    @Override
    public String getHint() {
        return "요청 시간["+ requestTime +"]은 서버 시간["+ standardTime +"]보다 미래 시간입니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
