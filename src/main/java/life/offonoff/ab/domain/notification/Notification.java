package life.offonoff.ab.domain.notification;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "notification_type")
public abstract class Notification extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiverType; // TODO:필요에 따라 삭제하셔도 됩니다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member receiver;

    @ColumnDefault("false")
    private Boolean isRead = false;

    public Notification(String receiverType, Member receiver) {
        this.receiverType = receiverType;
        this.receiver = receiver;
        receiver.addNotification(this);
    }

    //== Method ==//
    public void read() {
        this.isRead = true;
    }

    public abstract String getType();

    public boolean isNotifiedTo(Member member) {
        return this.receiver.equals(member);
    }
}
