package life.offonoff.ab.repository;

import life.offonoff.ab.domain.topic.choice.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
}
