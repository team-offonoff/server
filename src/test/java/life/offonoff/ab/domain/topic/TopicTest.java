package life.offonoff.ab.domain.topic;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TopicTest {

    @Test
    @DisplayName("투표 후에 choice의 voteCount가 증가한다.")
    void increase_choicesVoteCount_after_vote() {
        // given
        Topic topic = createRandomTopic();
        Choice choice = createChoice(topic, ChoiceOption.CHOICE_A, null);

        Member voter = createRandomMember();

        // when
        Vote vote = createVote(choice.getChoiceOption());
        vote.associate(voter, topic);

        // then
        assertThat(choice.getVoteCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("투표 수정 후에 이전 choice는 voteCount가 감소 / 수정 choice는 증가한다.")
    void choicesVoteCount_after_modify_vote() {
        // given
        Topic topic = createRandomTopic();
        Choice choiceA = createChoice(topic, ChoiceOption.CHOICE_A, null);
        Choice choiceB = createChoice(topic, ChoiceOption.CHOICE_B, null);

        Member voter = createRandomMember();

        Vote vote = createVote(choiceA.getChoiceOption());
        vote.associate(voter, topic);

        // when
        topic.changeVotedChoiceOption(choiceA.getChoiceOption(), choiceB.getChoiceOption());

        // then
        assertAll(
                () -> assertThat(choiceA.getVoteCount()).isEqualTo(0),
                () -> assertThat(choiceB.getVoteCount()).isEqualTo(1)
        );
    }
}