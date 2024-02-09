package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public class NotKoreanEnglishNumberException extends AbException {
    private static final String MESSAGE = "한글, 영문, 숫자만 가능해요.";
    private static final AbCode abCode = AbCode.NOT_KOREAN_ENGLISH_NUMBER;
    private final String text;
    public NotKoreanEnglishNumberException(String text) {
        super(MESSAGE);
        this.text = text;
    }

    @Override
    public String getHint() {
        return "필드["+ text +"]에 한글, 영문, 숫자가 아닌 문자가 포함되어있습니다.";
    }

    @Override
    public int getHttpStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public AbCode getAbCode() {
        return abCode;
    }
}
