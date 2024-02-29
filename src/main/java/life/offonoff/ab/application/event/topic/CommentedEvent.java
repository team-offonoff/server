package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.domain.comment.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentedEvent {

    private final Comment comment;
}
