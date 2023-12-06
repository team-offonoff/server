package life.offonoff.ab.exception;

public class MemberNotVoteException extends IllegalArgumentException {
    private static final String MESSAGE = "해당 멤버가 투표하지 않은 토픽입니다.";
    private static final AbCode AB_CODE = AbCode.MEMBER_NOT_VOTE;
    private final Long memberId;
    private final Long topicId;

    public MemberNotVoteException(Long memberId, Long topicId) {
        super(MESSAGE);
        this.memberId = memberId;
        this.topicId = topicId;
    }


    @Override
    public String getHint() {
        return "해당 멤버[id="+memberId+"]가 투표하지 않은 토픽[id="+topicId+"]입니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
