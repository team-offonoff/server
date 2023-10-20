package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class UnableToVoteException extends UnableToProcessException {
    private static final String MESSAGE = "투표를 할 수 없습니다.";
    private static final AbCode AB_CODE = AbCode.UNABLE_TO_VOTE;
    private final LocalDateTime deadline;

    public UnableToVoteException(final LocalDateTime deadline) {
        super(MESSAGE);
        this.deadline = deadline;
    }

    @Override
    public String getHint() {
        return "해당 토픽의 투표 기한은 [" + deadline + "] 까지입니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
