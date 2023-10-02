package life.offonoff.ab.repository;

import life.offonoff.ab.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findFirstByName(String name);
}
