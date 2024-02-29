package life.offonoff.ab.application.notification;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.domain.TestEntityUtil.TestMember;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.comment.LikedComment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Role;
import life.offonoff.ab.domain.notification.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.notification.VoteResultNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.notification.NotificationResponse;
import life.offonoff.ab.web.response.notification.message.CommentOnTopicNotificationMessage;
import life.offonoff.ab.web.response.notification.message.VoteCountOnTopicNotificationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static life.offonoff.ab.domain.TestEntityUtil.createRandomMember;
import static life.offonoff.ab.domain.TestEntityUtil.createRandomTopic;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@DisplayName("NoticeService 스프링 컨테이너 통합테스트")
public class NotificationServiceIntegrationTest {

    @Autowired
    NotificationService notificationService;
    @Value("${ab.notification.vote_on_topic.count_unit}")
    int voteCountUnit;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("VoteCountOnTopic 알림 생성한다.")
    void create_VoteCountOnTopicNotification() {
        // given
        Member author = createRandomMember();
        em.persist(author);

        Topic topic = TestTopic.builder()
                .author(author)
                .voteCount(voteCountUnit)
                .build().buildTopic();
        em.persist(topic);

        notificationService.notifyVoteCountOnTopic(topic);

        // when
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(author.getId());

        // then
        assertThat(responses.get(0).getMessage()).isInstanceOf(VoteCountOnTopicNotificationMessage.class);
    }

    @Test
    @DisplayName("토픽 삭제 시 토픽 투표 결과 알림을 삭제한다.")
    void delete_VoteResultNotification_when_topic_deleted() {
        // given
        // author
        Member author = TestMember.builder()
                .nickname("author")
                .build().buildMember();
        em.persist(author);

        // topic
        Topic topic = createRandomTopic();
        em.persist(topic);

        // notification
        VoteResultNotification voteResultNotification = new VoteResultNotification(author, topic);
        em.persist(voteResultNotification);

        // when
        topicRepository.delete(topic);

        // then
        assertThat(notificationService.findAllByReceiverId(author.getId())).isEmpty();
    }


    @Test
    @DisplayName("토픽 삭제 시 토픽 투표 수 알림을 삭제한다.")
    void delete_VoteCountOnTopicNotification_when_topic_deleted() {
        // given
        // author
        Member author = TestMember.builder()
                .nickname("author")
                .build().buildMember();
        em.persist(author);

        // topic
        Topic topic = createRandomTopic();
        em.persist(topic);

        // notification
        VoteCountOnTopicNotification notification = new VoteCountOnTopicNotification(topic);
        em.persist(notification);

        // when
        topicRepository.delete(topic);

        // then
        assertThat(notificationService.findAllByReceiverId(author.getId())).isEmpty();
    }

    @Test
    @DisplayName("댓글을 작성하면 토픽 작성자에게 댓글 알림을 생성한다.")
    void create_CommentOnTopicNotification_when_commented_by_voter() {
        // given
        Member author = TestMember.builder()
                .nickname("author")
                .build().buildMember();
        em.persist(author);

        Topic topic = TestTopic.builder()
                .author(author)
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();
        em.persist(topic);

        Member commenter = TestMember.builder()
                .nickname("commenter")
                .build().buildMember();
        em.persist(commenter);

        Comment comment = new Comment(commenter, topic, ChoiceOption.CHOICE_A, "content");
        em.persist(comment);

        notificationService.notifyCommentOnTopic(comment);

        // when
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(author.getId());

        // then
        assertThat(responses.get(0).getMessage()).isInstanceOf(CommentOnTopicNotificationMessage.class);
    }

    @Test
    @DisplayName("댓글을 작성하면 토픽 작성자에게 댓글 알림을 생성한다.")
    void does_not_create_CommentOnTopicNotification_when_commented_by_author() {
        // given
        Member author = TestMember.builder()
                .nickname("author")
                .build().buildMember();
        em.persist(author);

        Topic topic = TestTopic.builder()
                .author(author)
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();
        em.persist(topic);

        Comment comment = Comment.createAuthorsComment(author, topic, "content");
        em.persist(comment);

        notificationService.notifyCommentOnTopic(comment);

        // when
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(author.getId());

        // then
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("댓글이 삭제되면 댓글 관련 알림은 삭제한다.")
    void delete_CommentOnTopicNotification_when_comment_deleted() {
        // given
        Member author = TestMember.builder()
                .nickname("author")
                .build().buildMember();
        em.persist(author);

        Topic topic = TestTopic.builder()
                .author(author)
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();
        em.persist(topic);

        Member commenter = TestMember.builder()
                .nickname("commenter")
                .role(Role.USER)
                .build().buildMember();
        em.persist(commenter);

        Comment comment = new Comment(commenter, topic, ChoiceOption.CHOICE_A, "content");
        em.persist(comment);

        notificationService.notifyCommentOnTopic(comment);

        // when
        comment.remove();
        em.remove(comment);

        // then
        assertThat(notificationService.findAllByReceiverId(author.getId())).isEmpty();
    }

    @Test
    @DisplayName("토픽이 삭제되면 댓글 관련 알림은 삭제한다.")
    void delete_CommentOnTopicNotification_when_topic_deleted(){
        // given
        Member author = TestMember.builder()
                .nickname("author")
                .role(Role.USER)
                .build().buildMember();
        em.persist(author);

        Topic topic = TestTopic.builder()
                .author(author)
                .title("title")
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();
        em.persist(topic);

        Member commenter = TestMember.builder()
                .nickname("commenter")
                .role(Role.USER)
                .build().buildMember();
        em.persist(commenter);

        Comment comment = new Comment(commenter, topic, ChoiceOption.CHOICE_A, "content");
        em.persist(comment);

        notificationService.notifyCommentOnTopic(comment);

        // when
        em.remove(topic);

        // then
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(author.getId());
        assertThat(responses).isEmpty();
    }


    @Test
    @DisplayName("댓글 좋아요 시 댓글 작성자에게 댓글 좋아요 알림을 생성한다.")
    void create_LikeInCommentNotification_when_comment_liked_by_liker() {
        // given

          // member
        Member liker = TestMember.builder()
                .nickname("liker")
                .build().buildMember();
        em.persist(liker);

        Member author = TestMember.builder()
                .nickname("author")
                .build().buildMember();
        em.persist(author);

        Member commenter = TestMember.builder()
                .nickname("commenter")
                .role(Role.USER)
                .build().buildMember();
        em.persist(commenter);

          // topic
        Topic topic = TestTopic.builder()
                .author(author)
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();
        em.persist(topic);

          // comment
        Comment comment = new Comment(commenter, topic, ChoiceOption.CHOICE_A, "content");
        em.persist(comment);

        LikedComment likedComment = new LikedComment(liker, comment);

        notificationService.notifyLikeInComment(likedComment);

        // when
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(commenter.getId());

        // then
        assertThat(responses).isNotEmpty();
    }

    @Test
    @DisplayName("Liker == Writer 면 알림을 생성하지 않는다.")
    void does_not_create_LikeInCommentNotification_when_liker_is_writer() {
        // given

        // member
        Member author = TestMember.builder()
                .nickname("author")
                .build().buildMember();
        em.persist(author);

        Member commenter = TestMember.builder()
                .nickname("commenter")
                .role(Role.USER)
                .build().buildMember();
        em.persist(commenter);

        // topic
        Topic topic = TestTopic.builder()
                .author(author)
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();
        em.persist(topic);

        // comment
        Comment comment = new Comment(commenter, topic, ChoiceOption.CHOICE_A, "content");
        em.persist(comment);

        LikedComment likedComment = new LikedComment(commenter, comment);

        notificationService.notifyLikeInComment(likedComment);

        // when
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(commenter.getId());

        // then
        assertThat(responses).isEmpty();
    }


    @Test
    @DisplayName("댓글이 삭제되면 댓글 좋아요 알림은 삭제된다.")
    void delete_LikeInCommentNotification_when_comment_deleted() {
        // given

          // member
        Member liker = TestMember.builder()
                .nickname("liker")
                .build().buildMember();
        em.persist(liker);

        Member author = TestMember.builder()
                .nickname("author")
                .build().buildMember();
        em.persist(author);

        Member commenter = TestMember.builder()
                .nickname("commenter")
                .role(Role.USER)
                .build().buildMember();
        em.persist(commenter);

          // topic
        Topic topic = TestTopic.builder()
                .author(author)
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();
        em.persist(topic);

          // comment
        Comment comment = new Comment(commenter, topic, ChoiceOption.CHOICE_A, "content");
        em.persist(comment);

        LikedComment likedComment = new LikedComment(commenter, comment);

        notificationService.notifyLikeInComment(likedComment);

        // when
        comment.remove();
        em.remove(comment);

        // then
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(commenter.getId());
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("토픽이 삭제되면 토픽에 달린 댓글들의 좋아요 알림은 삭제된다.")
    void delete_LikeInCommentNotification_when_topic_deleted() {
        // given

        // member
        Member liker = TestMember.builder()
                .nickname("liker")
                .build().buildMember();
        em.persist(liker);

        Member author = TestMember.builder()
                .nickname("author")
                .build().buildMember();
        em.persist(author);

        Member commenter = TestMember.builder()
                .nickname("commenter")
                .role(Role.USER)
                .build().buildMember();
        em.persist(commenter);

        // topic
        Topic topic = TestTopic.builder()
                .author(author)
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();
        em.persist(topic);

        // comment
        Comment comment = new Comment(commenter, topic, ChoiceOption.CHOICE_A, "content");
        em.persist(comment);

        LikedComment likedComment = new LikedComment(commenter, comment);

        notificationService.notifyLikeInComment(likedComment);

        // when
        em.remove(topic);

        // then
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(commenter.getId());
        assertThat(responses).isEmpty();
    }
}
