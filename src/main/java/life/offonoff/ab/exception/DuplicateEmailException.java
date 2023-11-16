package life.offonoff.ab.exception;

public class DuplicateEmailException extends DuplicateException {

    private static final String MESSAGE = "중복된 이메일입니다.";


    public DuplicateEmailException() {
        super(MESSAGE);
    }

    @Override
    public String getHint() {
        return "중복되지 않은 이메일을 입력하세요.";
    }

    @Override
    public int getHttpStatusCode() {
        return 400;
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.DUPLICATE_EMAIL;
    }
}
