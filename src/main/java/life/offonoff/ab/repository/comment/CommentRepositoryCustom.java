package life.offonoff.ab.repository.comment;

import life.offonoff.ab.domain.comment.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentRepositoryCustom {

    Slice<Comment> findAll(Long topicId, Pageable pageable);
}
