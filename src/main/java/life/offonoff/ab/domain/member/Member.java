package life.offonoff.ab.domain.member;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.notice.Notification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.vote.Vote;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 40)
    private String nickname;

    private String email;

    private String password;

    @Embedded
    private NotificationEnabled notificationEnabled;

    @OneToMany(mappedBy = "publishMember")
    private List<Topic> publishedTopics = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<HiddenTopic> hiddenTopics = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    private int active = 1;

    //== Constructor ==//
    public Member(String name, String nickname, NotificationEnabled notificationEnabled) {
        this.name = name;
        this.nickname = nickname;
        this.notificationEnabled = notificationEnabled;
    }

    //== 연관관계 매핑 ==//
    public void publishTopic(Topic topic) {
        publishedTopics.add(topic);
    }

    public void hideTopic(HiddenTopic hiddenTopic) {
        hiddenTopics.add(hiddenTopic);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void addVote(Vote vote) {
        votes.add(vote);
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    //== Method ==//
    public void inactive() {
        this.active = 0;
    }

    public boolean hideAlready(Topic topic) {
        return hiddenTopics.stream()
                .anyMatch(h -> h.has(topic));
    }

    public void cancelHide(Topic topic) {
        hiddenTopics.removeIf(h -> h.has(topic));
    }

    public boolean votedAlready(Topic topic) {
        return votes.stream()
                .anyMatch(v -> v.has(topic));
    }

    public void cancelVote(Topic topic) {
        this.votes.removeIf(v -> v.has(topic));
    }

    public void readNotification(Notification notification) {
        notification.check();
    }
}


