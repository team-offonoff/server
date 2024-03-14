package life.offonoff.ab.repository;

import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {

    @Modifying
    @Query("update Choice c set c.voteCount = c.voteCount + 1 where c.topic.id = :topicId and c.choiceOption = :choiceOption")
    void increaseVoteCountOfChoice(Long topicId, ChoiceOption choiceOption);

    @Modifying
    @Query("update Choice c set c.voteCount = c.voteCount - 1 where c.topic.id = :topicId and c.choiceOption = :choiceOption")
    void decreaseVoteCountOfChoice(Long topicId, ChoiceOption choiceOption);
}
