package life.offonoff.ab.domain.member;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.notice.Notification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.vote.Vote;
import lombok.*;

import java.time.LocalDate;
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

    // 인증 정보
    @Embedded
    private AuthenticationInfo authInfo;
    // 개인 정보
    @Embedded
    private PersonalInfo personalInfo;
    // 알람 여부 정보
    @Embedded
    private NotificationEnabled notificationEnabled;
    // 마케팅 수신 동의
    private Boolean listenMarketing;

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
    public Member(String name, String nickname, LocalDate birth, Gender gender, String job, NotificationEnabled notificationEnabled) {
        this.personalInfo = new PersonalInfo(name, nickname, birth, gender, job);
        this.notificationEnabled = notificationEnabled;
    }

    public Member(String email, String password, Provider provider) {
        this.authInfo = new AuthenticationInfo(email, password, provider);
        this.notificationEnabled = NotificationEnabled.allEnabled();
    }

    public void registerAuthInfo(AuthenticationInfo authInfo) {
        this.authInfo = authInfo;
    }

    public void registerPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
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
    public JoinStatus getJoinStatus() {
        if (authInfo == null) {
            return JoinStatus.EMPTY;
        }

        if (personalInfo == null) {
            return JoinStatus.AUTH_REGISTERED;
        }

        if (listenMarketing == null) {
            return JoinStatus.PERSONAL_REGISTERED;
        }

        return JoinStatus.COMPLETE;
    }

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

    //== GETTER ==//
    public Role getRole() {
        return authInfo.getRole();
    }

    public String getPassword() {
        return authInfo.getPassword();
    }

    public String getNickname() {
        return personalInfo.getNickname();
    }
}


