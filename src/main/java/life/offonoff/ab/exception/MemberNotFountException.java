package life.offonoff.ab.exception;


public class MemberNotFountException extends NotFoundException {
    private static final String MESSAGE = "존재하지 않는 회원 입니다.";
    private static final AbCode AB_CODE = AbCode.MEMBER_NOT_FOUND;
    private final Long memberId;

    public MemberNotFountException(final Long memberId) {
        super(MESSAGE);
        this.memberId = memberId;
    }

    @Override
    public String getHint() {
        return "회원 id [" + memberId + "]는 존재하지 않습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
