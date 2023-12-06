package life.offonoff.ab.exception;

public class VoteByAuthorException extends IllegalArgumentException {
    private static final String MESSAGE = "토픽 작성자는 투표를 할 수 없습니다.";
    private static final AbCode AB_CODE = AbCode.VOTED_BY_AUTHOR;
    private final Long topicId;
    private final Long memberId;

    public VoteByAuthorException(Long topicId, Long memberId) {
        super(MESSAGE);
        this.topicId = topicId;
        this.memberId = memberId;
    }

    @Override
    public String getHint() {
        return "작성자[id="+memberId+"]는 토픽[id="+topicId+"]에 투표할 수 없습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AB_CODE;
    }
}
