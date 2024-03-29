package life.offonoff.ab.exception;

public class MemberByEmailNotFoundException extends MemberNotFoundException {

    private final String email;

    public MemberByEmailNotFoundException(final String email) {
        super();
        this.email = email;
    }

    @Override
    public String getHint() {
        return "회원 email [" + email + "]은 존재하지 않습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.MEMBER_NOT_FOUND;
    }
}
