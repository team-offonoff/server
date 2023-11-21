package life.offonoff.ab.exception;

public class DuplicateEmailException extends DuplicateException {

    private static final String MESSAGE = "중복된 이메일입니다.";
    private final String email;

    public DuplicateEmailException(String email) {
        super(MESSAGE);
        this.email = email;
    }

    @Override
    public String getHint() {
        return "이메일 [" + email + "]은 사용 중입니다.";
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
