package life.offonoff.ab.repository.keyword;

import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.topic.TopicSide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long>, KeywordRepositoryCustom {
    Optional<Keyword> findByNameAndSide(String name, TopicSide side);
}
