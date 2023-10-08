package life.offonoff.ab.repository.topic;

import life.offonoff.ab.domain.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public interface TopicRepository extends JpaRepository<Topic, Long>, TopicRepositoryCustom {
}
