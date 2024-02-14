package life.offonoff.ab.repository.comment;

import life.offonoff.ab.configuration.TestJPAConfig;
import life.offonoff.ab.domain.comment.Comment;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Provider;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

import static life.offonoff.ab.domain.TestEntityUtil.createRandomMember;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TestJPAConfig.class)
@EnableJpaAuditing
@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    CommentRepository commentRepository;

    @Test
    void findFirstByTopicIdOrderByCreatedAtDesc() {
        // given
        Member member = memberRepository.save(new Member("email", "pass", Provider.NONE));
        Topic topic = topicRepository.save(new Topic(createRandomMember(), "topic", TopicSide.TOPIC_B));

        commentRepository.save(new Comment(member, topic, ChoiceOption.CHOICE_A, "content1"));
        commentRepository.save(new Comment(member, topic, ChoiceOption.CHOICE_A, "content2"));
        Comment lastComment = commentRepository.save(new Comment(member, topic, ChoiceOption.CHOICE_A, "content3"));

        // when
        Optional<Comment> found = commentRepository.findFirstByTopicIdOrderByCreatedAtDesc(topic.getId());
        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get().getId()).isEqualTo(lastComment.getId());
    }

    @Test
    @DisplayName("writer id & topic id인 댓글 전부 삭제")
    void delete_writers_comments() {
        // given
        Member member = memberRepository.save(new Member("email", "pass", Provider.NONE));
        Topic topic = topicRepository.save(new Topic(member, "topic", TopicSide.TOPIC_B));
        commentRepository.save(new Comment(member, topic, ChoiceOption.CHOICE_A, "content1"));
        commentRepository.save(new Comment(member, topic, ChoiceOption.CHOICE_A, "content2"));

        Member member2 = memberRepository.save(new Member("email2", "pass", Provider.NONE));
        commentRepository.save(new Comment(member2, topic, ChoiceOption.CHOICE_A, "content3"));

        // when
        int removed = commentRepository.deleteAllByWriterIdAndTopicId(member.getId(), topic.getId());

        // then
        assertThat(removed).isEqualTo(2);
        assertThat(commentRepository.countAllByWriterIdAndTopicId(member.getId(), topic.getId()))
                .isEqualTo(0);
        assertThat(commentRepository.countAllByWriterIdAndTopicId(member2.getId(), topic.getId()))
                .isEqualTo(1);
        assertThat(memberRepository.count()).isEqualTo(2L);
    }
}
