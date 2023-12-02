package life.offonoff.ab.exception.auth.token;

import life.offonoff.ab.exception.AbCode;
import life.offonoff.ab.exception.auth.AuthorizationException;

public class UnsupportedAuthTokenTypeException extends AuthorizationException {

    private static final String MESSAGE = "지원되지 않는 타입의 토큰입니다.";

    private final String authTokenType;

    public UnsupportedAuthTokenTypeException(String authTokenType) {
        super(MESSAGE);

        this.authTokenType = authTokenType;
    }

    @Override
    public String getHint() {
        return "[" + authTokenType + "] 타입의 토큰은 지원하지 않습니다.";
    }

    @Override
    public int getHttpStatusCode() {
        return 0;
    }

    @Override
    public AbCode getAbCode() {
        return null;
    }
}
