package life.offonoff.ab.repository.comment;

import life.offonoff.ab.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    Optional<Comment> findFirstByTopicIdOrderByCreatedAtDesc(Long topicId);
}
