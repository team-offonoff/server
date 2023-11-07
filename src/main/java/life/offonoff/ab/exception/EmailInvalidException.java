package life.offonoff.ab.exception;


public class EmailInvalidException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 이메일 입니다.";
    private static final AbCode AB_CODE = AbCode.INVALID_EMAIL;
    private final String email;

    public EmailInvalidException(final String email) {
        super(MESSAGE);
        this.email = email;
    }

    @Override
    public String getHint() {
        return "이메일 [" + email + "]는 존재하지 않습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
