package life.offonoff.ab.exception;

public class IllegalPasswordException extends IllegalArgumentException {

    private static final String MESSAGE = "비밀번호가 일치하지 않습니다.";

    public IllegalPasswordException() {
        super(MESSAGE);
    }

    @Override
    public String getHint() {
        return "올바른 비밀번호를 입력해주세요.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.ILLEGAL_PASSWORD;
    }
}
