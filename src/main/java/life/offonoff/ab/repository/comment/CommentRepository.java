package life.offonoff.ab.repository.comment;

import life.offonoff.ab.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Optional<Comment> findFirstByTopicIdOrderByCreatedAtDesc(Long topicId);

    int countAllByWriterIdAndTopicId(Long writerId, Long topicId);

    @Modifying
    @Query("delete from Comment c where c.writer.id = :writerId and c.topic.id = :topicId")
    int deleteAllByWriterIdAndTopicId(Long writerId, Long topicId);
}
