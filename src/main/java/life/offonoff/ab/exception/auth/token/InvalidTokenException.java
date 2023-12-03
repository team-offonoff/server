package life.offonoff.ab.exception.auth.token;

import life.offonoff.ab.exception.AbCode;
import life.offonoff.ab.exception.AbException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AbException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(Exception exception) {
        super(exception);
    }

    public InvalidTokenException(String message, Exception cause) {
        super(message, cause);
    }

    @Override
    public String getHint() {
        return "유효하지 않은 토큰입니다.";
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.INVALID_TOKEN;
    }
}
