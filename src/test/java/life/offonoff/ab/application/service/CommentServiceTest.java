package life.offonoff.ab.application.service;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.common.LengthInfo;
import life.offonoff.ab.application.service.request.CommentRequest;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Role;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.CommentNotFoundException;
import life.offonoff.ab.exception.IllegalCommentStatusChangeException;
import life.offonoff.ab.exception.LengthInvalidException;
import life.offonoff.ab.web.response.CommentResponse;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class CommentServiceTest {

    @Autowired EntityManager em;

    @Autowired CommentService commentService;

    Member author;
    Member voter;
    Vote vote;
    Keyword keyword;
    Topic topic;

    @BeforeEach
    void beforeEach() {
        author = TestMember.builder()
                .build().buildMember(); em.persist(author);

        keyword = TestKeyword.builder()
                .name("key")
                .build().buildKeyword(); em.persist(keyword);

        topic = TestTopic.builder()
                .author(author)
                .keyword(keyword)
                .build().buildTopic(); em.persist(topic);

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
        CommentResponse response = commentService.register(author.getId(), new CommentRequest(topic.getId(), "content"));

        // then
        assertThat(response.getWritersVotedOption()).isEqualTo(null);
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
        commentService.likeCommentForMember(liker.getId(), comment.getId(), true);

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

    private Long createRandomMember() {
        Member member =  createCompletelyJoinedMember("email", "password", "nickname");
        em.persist(member);
        return member.getId();
    }

    private Long createRandomTopic() {
        Topic topic = createTopic(0, TopicSide.TOPIC_A);
        em.persist(topic);
        return topic.getId();
    }
}