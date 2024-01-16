package life.offonoff.ab.exception;

import life.offonoff.ab.domain.topic.choice.ChoiceOption;

public class AlreadyVotedException extends DuplicateException {

    private static final String MESSAGE = "이미 투표했습니다.";

    private final Long topicId;
    private final ChoiceOption votedOption;

    public AlreadyVotedException(Long topicId, ChoiceOption votedOption) {
        super(MESSAGE);
        this.topicId = topicId;
        this.votedOption = votedOption;
    }

    @Override
    public String getHint() {
        return "토픽[id=" + topicId + "]에 대해 투표 선택지[choiceOption=" + votedOption + "]로 이미 투표했습니다.";
    }

    @Override
    public AbCode getAbCode() {
        return AbCode.ALREADY_VOTED;
    }

    @Override
    public Object getPayload() {
        return votedOption;
    }
}
