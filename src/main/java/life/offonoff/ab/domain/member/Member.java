package life.offonoff.ab.domain.member;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.notice.Notification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.IllegalJoinStatusException;
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

    // 인증 정보
    @Embedded
    private AuthenticationInfo authInfo;
    // 개인 정보
    @Embedded
    private PersonalInfo personalInfo;
    // 역관 동의
    @Embedded
    private TermsEnabled termsEnabled;
    // 알람 여부 정보
    @Embedded
    private NotificationEnabled notificationEnabled;

    private String profileImageUrl;

    @OneToMany(mappedBy = "author")
    private List<Topic> publishedTopics = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "voter", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<HiddenTopic> hiddenTopics = new ArrayList<>();

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    private boolean active = true;

    //== Constructor ==//
    public Member(String email, String password, Provider provider) {
        this.authInfo = new AuthenticationInfo(email, password, provider);
        this.notificationEnabled = NotificationEnabled.allEnabled();
    }

    public void registerAuthInfo(AuthenticationInfo authInfo) {
        if (this.authInfo != null) {
            throw new IllegalJoinStatusException(getJoinStatus());
        }

        this.authInfo = authInfo;
    }

    public void registerPersonalInfo(PersonalInfo personalInfo) {
        if (this.personalInfo != null) {
            throw new IllegalJoinStatusException(getJoinStatus());
        }

        this.personalInfo = personalInfo;
    }

    public void agreeTerms(TermsEnabled termsEnabled) {
        if (this.termsEnabled != null) {
            throw new IllegalJoinStatusException(getJoinStatus());
        }

        this.termsEnabled = termsEnabled;
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

    public JoinStatus getJoinStatus() {
        if (authInfo == null) {
            return JoinStatus.EMPTY;
        }

        if (personalInfo == null) {
            return JoinStatus.AUTH_REGISTERED;
        }

        if (termsEnabled == null) {
            return JoinStatus.PERSONAL_REGISTERED;
        }

        return JoinStatus.COMPLETE;
    }

    //== Method ==//
    public void activate(boolean active) {
        this.active = active;
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

    public void readNotification(Notification notification) {
        notification.check();
    }

    public boolean joinCompleted() {
        return this.getJoinStatus() == JoinStatus.COMPLETE;
    }
}


