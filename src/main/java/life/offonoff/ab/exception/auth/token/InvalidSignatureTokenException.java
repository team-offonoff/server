package life.offonoff.ab.exception.auth.token;

import life.offonoff.ab.exception.AbCode;

public class InvalidSignatureTokenException extends InvalidTokenException {

    private static final String MESSAGE = "이 서버로 부터 생성된 토큰이 아닙니다.";

    public InvalidSignatureTokenException() {
        super(MESSAGE);
    }

    public InvalidSignatureTokenException(Exception exception) {
        super(MESSAGE, exception);
    }

    @Override
    public String getHint() {
        return "서버로 부터 토큰을 발급받아 요청해주세요.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.INVALID_SIGNATURE_TOKEN;
    }
}
