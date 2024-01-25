package life.offonoff.ab.domain.comment;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HatedComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member hater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    public HatedComment(Member member, Comment comment) {
        this.hater = member;
        this.comment = comment;
    }

    public boolean has(Comment comment) {
        return this.comment.getId().equals(comment.getId());
    }
}
