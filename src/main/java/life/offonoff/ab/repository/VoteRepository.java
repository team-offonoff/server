package life.offonoff.ab.repository;

import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.web.response.topic.choice.ChoiceResponseWithCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByVoterIdAndTopicId(Long voterId, Long topicId);

    boolean existsByVoterIdAndTopicId(Long memberId, Long topicId);
}
