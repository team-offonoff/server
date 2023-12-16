package life.offonoff.ab.exception;

import life.offonoff.ab.application.service.common.LengthInfo;
import org.springframework.http.HttpStatus;

public class LengthInvalidException extends AbException {
    private static final String MESSAGE = "입력 길이를 확인해주세요.";
    private static final AbCode AB_CODE = AbCode.INVALID_LENGTH_OF_FIELD;
    private final String fieldName;
    private final Integer minLength;
    private final Integer maxLength;

    public LengthInvalidException(String fieldName, LengthInfo lengthInfo) {
        this(fieldName, lengthInfo.getMinLength(), lengthInfo.getMaxLength());
    }

    public LengthInvalidException(String fieldName, Integer minLength, Integer maxLength) {
        super(MESSAGE);
        this.fieldName = fieldName;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public String getHint() {
        return fieldName + "의 길이는 "+minLength+"~"+maxLength+" 사이여야 합니다.";
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
