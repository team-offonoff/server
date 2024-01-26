package life.offonoff.ab.exception;

import org.springframework.http.HttpStatus;

public class MemberDeactivatedException extends AbException{
    private static final String MESSAGE = "탈퇴한 회원입니다.";
    private static final AbCode abCode = AbCode.DEACTIVATED_MEMBER;
    private final Long memberId;
    private final String email;

    public MemberDeactivatedException(String email) {
        this(null, email);
    }

    public MemberDeactivatedException(Long memberId) {
        this(memberId, null);
    }

    public MemberDeactivatedException(Long memberId, String email) {
        super(MESSAGE);
        this.memberId = memberId;
        this.email = email;
    }

    @Override
    public String getHint() {
        return "탈퇴한 멤버[id="+memberId+", email="+email+"]입니다.";
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
