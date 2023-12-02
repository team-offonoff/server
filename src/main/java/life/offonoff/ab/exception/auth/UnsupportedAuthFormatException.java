package life.offonoff.ab.exception.auth;

import life.offonoff.ab.exception.AbCode;
import org.springframework.http.HttpStatus;

public class UnsupportedAuthFormatException extends AuthorizationException {

    private static final String MESSAGE = "지원하지 않는 형식의 인가 정보입니다.";

    private final String requested;

    public UnsupportedAuthFormatException(String requested) {
        super(MESSAGE);

        this.requested = requested;
    }

    @Override
    public String getHint() {
        return "지원하지 않는 형식의 인가 정보입니다. {" + requested + "}";
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.UNSUPPORTED_AUTH_FORMAT;
    }
}
