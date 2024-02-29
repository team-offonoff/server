package life.offonoff.ab.application.event.topic;

import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.comment.LikedComment;
import life.offonoff.ab.domain.member.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
@RequiredArgsConstructor
public class CommentLikedEvent {

    private final LikedComment likedComment;
}
