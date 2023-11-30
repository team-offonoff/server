package life.offonoff.ab.repository.topic;

import life.offonoff.ab.domain.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {
    Optional<Topic> findByIdAndActiveTrue(Long topicId);
}
