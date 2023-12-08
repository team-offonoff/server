package life.offonoff.ab.exception;

import life.offonoff.ab.domain.member.JoinStatus;

public class IllegalJoinStatusException extends AbException {

    private static final String MESSAGE = "올바른 회원 등록 단계가 아닙니다.";
    private final Long memberId;
    private final JoinStatus joinStatus;

    public IllegalJoinStatusException(Long memberId, JoinStatus joinStatus) {
        super(MESSAGE);
        this.memberId = memberId;
        this.joinStatus = joinStatus;
    }

    @Override
    public Object getPayload() {
        return memberId;
    }

    @Override
    public String getHint() {
        return "현재 회원의 가입 진행 상태는 [" + joinStatus + "] 입니다. 다음 단계를 등록해주세요.";
    }

    @Override
    public int getHttpStatusCode() {
        return 400;
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.ILLEGAL_JOIN_STATUS;
    }
}
