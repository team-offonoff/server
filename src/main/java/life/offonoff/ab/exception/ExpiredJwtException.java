package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public class ExpiredJwtException extends AbException {
    private static final String MESSAGE = "만료된 JWT입니다.";

    public ExpiredJwtException() {
        super(MESSAGE);
    }

    @Override
    public String getHint() {
        return "JWT를 재발급받아야 합니다.";
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.FORBIDDEN.value();
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.EXPIRED_JWT;
    }
}
