package life.offonoff.ab.exception;

public class MemberByIdNotFoundException extends MemberNotFoundException {
    private final Long memberId;

    public MemberByIdNotFoundException(final Long memberId) {
        super();
        this.memberId = memberId;
    }

    @Override
    public String getHint() {
        return "회원 id [" + memberId + "]는 존재하지 않습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.MEMBER_NOT_FOUND;
    }
}
