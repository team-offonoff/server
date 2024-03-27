package life.offonoff.ab.application.notification;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.comment.LikedComment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Role;
import life.offonoff.ab.domain.notification.DefaultNotification;
import life.offonoff.ab.domain.notification.VoteCountOnTopicNotification;
import life.offonoff.ab.domain.notification.VoteResultNotification;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.exception.IllegalReceiverException;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.notification.NotificationResponse;
import life.offonoff.ab.web.response.notification.message.CommentOnTopicNotificationMessage;
import life.offonoff.ab.web.response.notification.message.VoteCountOnTopicNotificationMessage;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final String[] touchedTables
            = {"notification", "comment", "topic", "member"};

    @AfterEach
    void tearDown() {
        // 이미 있던 트랜잭션은 닫아줌
        // 기존 트랜잭션이 롤백될 수도 있어서 테이블을 delete하려는 시도도 같이 롤백될 수가 있기 때문
        TestTransaction.end();
        TestTransaction.start();

        // 남아있는 데이터 삭제
        JdbcTestUtils.deleteFromTables(
                jdbcTemplate, touchedTables);
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

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

        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

        // notification
        VoteResultNotification voteResultNotification = VoteResultNotification.createForAuthor(topic);
        em.persist(voteResultNotification);

        // when
        topicRepository.delete(topic);
        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

        // notification
        VoteCountOnTopicNotification notification = new VoteCountOnTopicNotification(topic);
        em.persist(notification);

        // when
        topicRepository.delete(topic);

        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

        notificationService.notifyCommentOnTopic(comment);

        // when
        comment.remove();
        em.remove(em.merge(comment));
        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

        notificationService.notifyCommentOnTopic(comment);

        em.remove(em.merge(topic));

        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

        notificationService.notifyLikeInComment(likedComment);

        // when
        comment.remove();
        em.remove(em.merge(comment));
        commitTestTransactionAndRestart();

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

        commitTestTransactionAndRestart();

        notificationService.notifyLikeInComment(likedComment);

        // when
        em.remove(em.merge(topic));
        commitTestTransactionAndRestart();

        // then
        List<NotificationResponse> responses = notificationService.findAllByReceiverId(commenter.getId());
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("수신자가 알림을 읽게 되면 is_read -> true가 된다.")
    void read_notification() {
        // given
        Member receiver = TestMember.builder()
                .nickname("receiver")
                .build().buildMember();
        em.persist(receiver);

        DefaultNotification notification = new DefaultNotification(receiver, "title", "content");
        em.persist(notification);

        // when
        notificationService.readNotification(receiver.getId(), notification.getId());

        // then
        assertThat(notification.getIsRead()).isTrue();
    }

    @Test
    @DisplayName("수신자가 아닌 회원이 알림을 읽게 되면 예외가 발생한다.")
    void read_notification_exception_read_by_non_receiver() {
        // given
        Member receiver = TestMember.builder()
                .nickname("receiver")
                .build().buildMember();
        em.persist(receiver);

        Member nonReceiver = TestMember.builder()
                .nickname("nonReceiver")
                .build().buildMember();
        em.persist(nonReceiver);

        DefaultNotification notification = new DefaultNotification(receiver, "title", "content");
        em.persist(notification);

        // when
        assertThatThrownBy(() -> notificationService.readNotification(nonReceiver.getId(), notification.getId()))
                .isInstanceOf(IllegalReceiverException.class);
    }

    private void commitTestTransactionAndRestart() {
        // 현재 영속성 컨텍스트의 엔티티 상태 정보 db에 반영.
        // 실제 운영 중 notificationService는 엔티티의 상태가 이미 반영된 상태에서만 알림을 보낼 것이므로
        // 운영 상황과 같다.
        // ! 주의할 점은 실제로 데이터베이스에 커밋되므로 수동으로 롤백해줘야한다.
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
    }
}
