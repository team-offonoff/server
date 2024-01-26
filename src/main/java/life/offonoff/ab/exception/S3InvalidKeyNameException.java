package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public class S3InvalidKeyNameException extends AbException {
    private static final String MESSAGE = "올바르지 않은 keyname입니다.";
    private final String keyName;
    public S3InvalidKeyNameException(String keyName) {
        super(MESSAGE);
        this.keyName = keyName;
    }

    @Override
    public String getHint() {
        return "요청한 keyName["+keyName+"]이 올바르지 않습니다.";
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.S3_INVALID_KEY_NAME;
    }
}
