package life.offonoff.ab.exception.auth.token;

import life.offonoff.ab.exception.AbCode;
import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends InvalidTokenException {

    private static final String MESSAGE = "만료된 토큰입니다.";

    public ExpiredTokenException() {
        super(MESSAGE);
    }

    public ExpiredTokenException(Exception exception) {
        super(MESSAGE, exception);
    }

    @Override
    public String getHint() {
        return "토큰을 재발급받아야 합니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.EXPIRED_TOKEN;
    }
}
