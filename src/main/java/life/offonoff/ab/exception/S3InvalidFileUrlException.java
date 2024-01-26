package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public class S3InvalidFileUrlException extends AbException {
    private static final String MESSAGE = "올바르지 않은 파일 url입니다.";
    private final String fileUrl;
    public S3InvalidFileUrlException(String fileUrl) {
        super(MESSAGE);
        this.fileUrl = fileUrl;
    }

    @Override
    public String getHint() {
        return "요청한 URL["+fileUrl+"]이 올바르지 않습니다.";
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.S3_INVALID_FILE_URL;
    }
}
