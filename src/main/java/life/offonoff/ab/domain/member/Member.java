package life.offonoff.ab.domain.member;

import jakarta.persistence.*;
import life.offonoff.ab.domain.BaseEntity;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.comment.HatedComment;
import life.offonoff.ab.domain.comment.LikedComment;
import life.offonoff.ab.domain.notification.Notification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.vote.Vote;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Topic> publishedTopics = new ArrayList<>();
    // TODO : 삭제된 멤버의 댓글 유지 여부
    @OneToMany(mappedBy = "writer", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "liker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikedComment> likedComments = new ArrayList<>();

    @OneToMany(mappedBy = "hater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HatedComment> hatedComments = new ArrayList<>();

    @OneToMany(mappedBy = "voter", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HiddenTopic> hiddenTopics = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    private boolean active = true;

    //== Constructor ==//
    public Member(String email, String password, Provider provider) {
        this(new AuthenticationInfo(email, password, provider));
    }

    public Member(AuthenticationInfo authInfo) {
        this.authInfo = authInfo;
        this.notificationEnabled = NotificationEnabled.allEnabled();
    }

    public void registerAuthInfo(AuthenticationInfo authInfo) {
        if (this.authInfo == null) {
            this.authInfo = authInfo;
        }
    }

    public void registerPersonalInfo(PersonalInfo personalInfo) {
        if (this.personalInfo == null) {
            this.personalInfo = personalInfo;
        }
    }

    public void agreeTerms(TermsEnabled termsEnabled) {
        if (this.termsEnabled == null) {
            this.termsEnabled = termsEnabled;
        }
    }

    //== 연관관계 매핑 ==//
    public void publishTopic(Topic topic) {
        publishedTopics.add(topic);
    }

    public void hideTopicIfNew(Topic topic) {
        if (hideAlready(topic)) {
            return;
        }

        HiddenTopic hiddenTopic = new HiddenTopic();
        hiddenTopic.associate(this, topic);

        hiddenTopics.add(hiddenTopic);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public LikedComment likeCommentIfNew(Comment comment) {
        if (likeAlready(comment)) {
            return null;
        }

        LikedComment likedComment = new LikedComment(this, comment);
        comment.increaseLikeCount();

        likedComments.add(likedComment);
        return likedComment;
    }

    public void hateCommentIfNew(Comment comment) {
        if (hateAlready(comment)) {
            return;
        }

        HatedComment hatedComment = new HatedComment(this, comment);
        comment.increaseHateCount();

        this.hatedComments.add(hatedComment);
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

        //== HIDE ==//
    public boolean hideAlready(Topic topic) {
        return hiddenTopics.stream()
                .anyMatch(h -> h.has(topic));
    }

    public void cancelHideIfExists(Topic topic) {
        if (hideAlready(topic)) {
            hiddenTopics.removeIf(h -> h.has(topic));
        }
    }

        //== VOTE ==//
    public boolean votedAlready(Topic topic) {
        return votes.stream()
                .anyMatch(v -> v.isFor(topic));
    }

    public void checkNotification(Notification notification) {
        notification.check();
    }

    public boolean joinCompleted() {
        return this.getJoinStatus() == JoinStatus.COMPLETE;
    }

    public boolean isAdmin() {
        return getRole().equals(Role.ADMIN);
    }

        //== HATE ==//
    public boolean hateAlready(Comment comment) {
        return hatedComments.stream()
                .anyMatch(h -> h.has(comment));
    }

    public void cancelHateIfExists(Comment comment) {
        boolean removed = hatedComments.removeIf(h -> h.has(comment));
        if (removed) {
            comment.decreaseHateCount();
        }
    }

        //== LIKE ==//
    public boolean likeAlready(Comment comment) {
        return likedComments.stream()
                .anyMatch(h -> h.has(comment));
    }

    public void cancelLikeIfExists(Comment comment) {
        boolean removed = likedComments.removeIf(l -> l.has(comment));
        if (removed) {
            comment.decreaseLikeCount();
        }
    }

    public void changeNotificationEnabled(NotificationEnabled notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public boolean isAuthorOf(Topic topic) {
        return this == topic.getAuthor();
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    public ChoiceOption getVotedOptionOfTopic(Topic topic) {
        return votes.stream()
                .filter(vote -> vote.isFor(topic))
                .map(Vote::getSelectedOption)
                .findAny()
                .orElse(null);
    }

    public void updateNickname(String nickname) {
        if (personalInfo.getNickname().equals(nickname)) {
            return;
        }
        this.personalInfo.updateNickname(nickname);
    }

    public void updateJob(String job) {
        if (personalInfo.getJob().equals(job)) {
            return;
        }
        this.personalInfo.updateJob(job);
    }

    public String updateProfileImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
        return this.profileImageUrl;
    }

    public boolean listenVoteResult() {
        return this.notificationEnabled.listeningVoteResult();
    }

    public boolean listenVoteCountOnTopic() {
        return this.notificationEnabled.listeningVoteCountOnTopic();
    }

    public boolean listenLikeInComment() {
        return this.notificationEnabled.listeningLikeInComment();
    }

    public boolean listenCommentOnTopic() {
        return this.notificationEnabled.listeningCommentOnTopic();
    }

    public void removeProfileImageUrl() {
        this.profileImageUrl = null;
    }
}