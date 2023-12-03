package life.offonoff.ab.exception.auth;

import life.offonoff.ab.exception.AbCode;
import org.springframework.http.HttpStatus;

public class EmptyAuthorizationException extends AuthorizationException {

    private static final String MESSAGE = "Authorization 헤더 값이 비어있습니다.";

    public EmptyAuthorizationException() {
        super(MESSAGE);
    }

    @Override
    public String getHint() {
        return "인증이 필요한 요청입니다.";
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.EMPTY_AUTHORIZATION;
    }
}
