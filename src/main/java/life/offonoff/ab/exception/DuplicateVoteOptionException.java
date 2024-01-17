package life.offonoff.ab.exception;

import life.offonoff.ab.domain.topic.choice.ChoiceOption;

public class DuplicateVoteOptionException extends DuplicateException {

    private static final String MESSAGE = "중복된 투표 선택지입니다.";

    private final Long topicId;
    private final ChoiceOption votedOption;

    public DuplicateVoteOptionException(Long topicId, ChoiceOption votedOption) {
        super(MESSAGE);
        this.topicId = topicId;
        this.votedOption = votedOption;
    }

    @Override
    public String getHint() {
        return "토픽[id : " + topicId + "]에 대해 동일한 투표 선택지[choiceOption : " + votedOption + "]로 수정할 수 없습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.DUPLICATE_VOTE_OPTION;
    }

    @Override
    public Object getPayload() {
        return votedOption;
    }
}
