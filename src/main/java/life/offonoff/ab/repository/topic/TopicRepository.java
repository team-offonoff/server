package life.offonoff.ab.repository.topic;

import life.offonoff.ab.domain.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {
    Optional<Topic> findByIdAndActiveTrue(Long topicId);

    boolean existsByIdAndActiveTrue(Long topicId);

    @Query("select t.commentCount from Topic t where t.id = :topicId")
    int findCommentCountById(Long topicId);
}
