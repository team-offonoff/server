package life.offonoff.ab.application.service;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.common.LengthInfo;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Role;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.comment.CommentRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.web.response.CommentResponse;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Transactional
@SpringBootTest
class CommentServiceTest {

    @Autowired EntityManager em;

    @Autowired CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    MemberRepository memberRepository;

    Member topicAuthor;
    Member voter;
    Vote vote;
    Keyword keyword;
    Topic topic;
    List<Choice> choices;

    @BeforeEach
    void beforeEach() {
        topicAuthor = TestMember.builder()
                .build().buildMember(); em.persist(topicAuthor);

        keyword = TestKeyword.builder()
                .name("key")
                .build().buildKeyword(); em.persist(keyword);

        topic = TestTopic.builder()
                .author(topicAuthor)
                .keyword(keyword)
                .build().buildTopic(); em.persist(topic);

        choices = List.of(createChoice(topic, ChoiceOption.CHOICE_A, null),
                          createChoice(topic, ChoiceOption.CHOICE_B, null));

        voter = TestMember.builder()
                .role(Role.USER)
                .build().buildMember(); em.persist(voter);

        vote = new Vote(ChoiceOption.CHOICE_A, LocalDateTime.now());
        vote.associate(voter, topic); em.persist(vote);
    }

    @Test
    @DisplayName("댓글 저장")
    void save_comment() {
        // given
        CommentRequest request = new CommentRequest(topic.getId(), "content");

        // when
        CommentResponse response = commentService.register(voter.getId(), request);

        // then
        assertThat(response.getTopicId()).isEqualTo(topic.getId());
    }

    @Test
    void register_withLongContent_throwsException() {
        ThrowingCallable code = () -> commentService.register(
                voter.getId(),
                new CommentRequest(
                        topic.getId(),
                        "c".repeat(LengthInfo.COMMENT_CONTENT.getMaxLength() + 1))
        );

        assertThatThrownBy(code)
                .isInstanceOf(LengthInvalidException.class);
    }

    @Test
    @DisplayName("댓글 저장후 토픽 댓글 수 증가")
    void save_comment_topic_comment_count() {
        // given
        int beforeCount = topic.getCommentCount();
        CommentRequest request = new CommentRequest(topic.getId(), "content");

        // when
        CommentResponse response = commentService.register(voter.getId(), request);

        // then
        assertThat(topic.getCommentCount()).isEqualTo(beforeCount + 1);
    }

    @Test
    @DisplayName("토픽 투표자의 댓글에 selectedOption 반영")
    void create_comment_by_voter() {

        // when
        CommentResponse response = commentService.register(voter.getId(), new CommentRequest(topic.getId(), "content"));

        // then
        assertThat(response.getWritersVotedOption()).isEqualTo(vote.getSelectedOption());
    }

    @Test
    @DisplayName("토픽 작성자의 댓글은 selectedOption 이 null")
    void create_comment_by_author() {
        // when
        CommentResponse response = commentService.register(topicAuthor.getId(), new CommentRequest(topic.getId(), "content"));

        // then
        assertThat(response.getWritersVotedOption()).isEqualTo(null);
    }

    @Test
    @DisplayName("토픽 작성자는 투표 없이 댓글 조회 가능하다.")
    void find_comments_by_topic_author() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // then
        assertDoesNotThrow(() -> commentService.findAll(topicAuthor.getId(), topic.getId(), pageable));
    }

    @Test
    @DisplayName("투표자는 댓글 조회가 가능하다.")
    void find_comments_by_voter() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // then
        assertDoesNotThrow(() -> commentService.findAll(voter.getId(), topic.getId(), pageable));
    }

    @Test
    @DisplayName("투표 없이 댓글 조회는 불가능하다.")
    void find_comments_by_non_voter_then_exception() {
        // given
        Long nonVoterId = createRandomMember();
        Pageable pageable = PageRequest.of(0, 10);

        // then
        assertThatThrownBy(() -> commentService.findAll(nonVoterId, topic.getId(), pageable))
                .isInstanceOf(UnableToViewCommentsException.class);
    }

    @Test
    @DisplayName("ADMIN에 의한 댓글 삭제")
    void delete_comment_by_admin() {
        // given
        Member adminMember = TestMember.builder()
                .role(Role.ADMIN)
                .build().buildMember(); em.persist(adminMember);

        Comment comment = new Comment(voter, topic, ChoiceOption.CHOICE_A,"content");
        em.persist(comment);

        // when
        commentService.deleteComment(adminMember.getId(), comment.getId());

        // then
        assertThat(topic.getCommentCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 작성자에 의한 댓글 삭제")
    void delete_comment_by_comment_writer() {
        // given
        Comment comment = new Comment(voter, topic, ChoiceOption.CHOICE_A, "content");
        em.persist(comment);

        // when
        commentService.deleteComment(voter.getId(), comment.getId());

        // then
        assertThat(topic.getCommentCount()).isEqualTo(0);
    }

    @Test
    void modifyComment() {
        Long commentId = commentService.register(
                voter.getId(),
                new CommentRequest(topic.getId(), "content")
        ).getCommentId();

        commentService.modifyMembersCommentContent(
                voter.getId(), commentId,
                "new content");

        Comment comment = em.find(Comment.class, commentId);
        assertThat(comment.getContent()).isEqualTo("new content");
    }

    @Test
    void modifyComment_withInvalidCommentId_throwsException() {

        Long commentId = commentService.register(
                voter.getId(),
                new CommentRequest(topic.getId(), "content")
        ).getCommentId();

        ThrowingCallable code = () -> commentService.modifyMembersCommentContent(
                voter.getId(),
                commentId + 1,
                "new content");

        assertThatThrownBy(code)
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void modifyComment_withLongContent_throwsException() {

        Long commentId = commentService.register(
                voter.getId(),
                new CommentRequest(topic.getId(), "content")
        ).getCommentId();

        ThrowingCallable code = () -> commentService.modifyMembersCommentContent(
                voter.getId(), commentId,
                "c".repeat(LengthInfo.COMMENT_CONTENT.getMaxLength() + 1));

        assertThatThrownBy(code)
                .isInstanceOf(LengthInvalidException.class);
    }

    @Test
    void modifyComment_withNonAuthor_throwsException() {

        Long commentId = commentService.register(
                voter.getId(),
                new CommentRequest(topic.getId(), "content")
        ).getCommentId();

        Long otherMemberId = createRandomMember();

        ThrowingCallable code = () -> commentService.modifyMembersCommentContent(
                otherMemberId,
                commentId, "new content");

        assertThatThrownBy(code)
                .isInstanceOf(IllegalCommentStatusChangeException.class);
    }

    @Test
    @DisplayName("댓글 Like 시에 Hate가 있다면 Hate 취소 후 좋아요")
    void like_with_cancel_hate() {
        // given
        Comment comment = Comment.createVotersComment(vote, "content");
        em.persist(comment);

        Member liker = TestMember.builder()
                .build().buildMember();
        em.persist(liker);

        liker.hateCommentIfNew(comment);

        // when
        commentService.likeCommentByMember(liker.getId(), comment.getId(), true);

        // then
        assertAll(
                () -> assertThat(liker.hateAlready(comment)).isFalse(),
                () -> assertThat(liker.likeAlready(comment)).isTrue(),
                () -> assertThat(comment.getHateCount()).isZero(),
                () -> assertThat(comment.getLikeCount()).isOne()
        );
    }

    @Test
    @DisplayName("댓글 Hate 시에 Like가 있다면 Like 취소 후 Hate")
    void hate_with_cancel_like() {
        // given
        Comment comment = Comment.createVotersComment(vote, "content");
        em.persist(comment);

        Member hater = TestMember.builder()
                .build().buildMember();
        em.persist(hater);

        hater.likeCommentIfNew(comment);

        // when
        commentService.hateCommentForMember(hater.getId(), comment.getId(), true);

        // then
        assertAll(
                () -> assertThat(hater.likeAlready(comment)).isFalse(),
                () -> assertThat(hater.hateAlready(comment)).isTrue(),
                () -> assertThat(comment.getLikeCount()).isZero(),
                () -> assertThat(comment.getHateCount()).isOne()
        );
    }

    @Test
    void reportCommentByMember_success() {
        // given
        Comment comment = new Comment(voter, topic, ChoiceOption.CHOICE_A,"content");
        em.persist(comment);
        Member reporter = TestEntityUtil.createRandomMember();
        em.persist(reporter);

        // when
        Executable code = () ->
                commentService.reportCommentByMember(comment.getId(), reporter.getId());

        // then
        assertDoesNotThrow(code);
    }

    @Test
    void reportCommentByMember_withNonExistentComment_exception() {
        // given
        Member reporter = TestEntityUtil.createRandomMember();
        em.persist(reporter);

        // when
        ThrowingCallable code = () ->
                commentService.reportCommentByMember(1L, reporter.getId());

        // then
        assertThatThrownBy(code)
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void reportCommentByMember_withDuplicateReport_exception() {
        // given
        Comment comment = new Comment(voter, topic, ChoiceOption.CHOICE_A,"content");
        em.persist(comment);
        Member reporter = TestEntityUtil.createRandomMember();
        em.persist(reporter);

        commentService.reportCommentByMember(comment.getId(), reporter.getId());

        // when
        ThrowingCallable code = () ->
                commentService.reportCommentByMember(comment.getId(), reporter.getId());

        // then
        assertThatThrownBy(code)
                .isInstanceOf(CommentReportDuplicateException.class);
    }

    private Long createRandomMember() {
        Member member =  createCompletelyJoinedMember("email", "password", "nickname");
        em.persist(member);
        return member.getId();
    }

    private Long createRandomTopic() {
        Topic topic = createRandomTopicByRandomMember(TopicSide.TOPIC_B);
        em.persist(topic);
        return topic.getId();
    }
}