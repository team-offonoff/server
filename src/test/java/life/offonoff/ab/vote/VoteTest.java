package life.offonoff.ab.vote;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class VoteTest {

    @Test
    @DisplayName("투표시 Topic의 vote count 증가")
    void vote_count() {
        // given
        int seq = 0;
        Member member = createMember("email", "password");

        Topic topic = createRandomTopicHavingChoices(ChoiceOption.CHOICE_A, ChoiceOption.CHOICE_B);

        // when
        Vote vote = createVote(ChoiceOption.CHOICE_A);
        vote.associate(member, topic);

        // then
        assertAll(
                () -> assertThat(member.getVotes()).contains(vote),
                () -> assertThat(topic.getVoteCount()).isNotEqualTo(1)
        );
    }
}