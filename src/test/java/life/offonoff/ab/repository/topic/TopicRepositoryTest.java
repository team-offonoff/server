package life.offonoff.ab.repository.topic;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.TopicStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.lang.reflect.Field;
import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TopicRepositoryTest {

    @Autowired
    TopicRepository repository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("투표 수 내림차순, 토픽 조회")
    void 토픽_조회_except_hidden() throws NoSuchFieldException, IllegalAccessException {
        // given
        int size = 5;

        Pageable pageable = createPageableDesc(0, size, "voteCount");
        Member member = createMember(1);
        List<Topic> topicList = creatAssociatedTopicList(size, TopicSide.TOPIC_A);

        for (Topic t : topicList) {
            setVoteCountByReflection(size--, t);
        }

        repository.saveAll(topicList);
        em.persist(member);

        // when
        Slice<Topic> topics = repository.findAllNotHidden(
                TopicStatus.VOTING,
                member.getId(),
                pageable
        );

        // then
        assertAll(
                () -> assertThat(topics.getSize()).isEqualTo(pageable.getPageSize()),
                () -> assertThat(topics.hasNext()).isFalse(),
                () -> assertThat(topics.getContent()).isEqualTo(topicList)
        );
    }

    @Test
    @DisplayName("투표 수 내림차순, 카테고리로 토픽 조회")
    void 토픽_조회_by_categoryId_except_hidden() throws NoSuchFieldException, IllegalAccessException {
        // given
        int size = 5;

        Pageable pageable = createPageableDesc(0, size, "voteCount");
        Member member = createMember(1);
        Category category = createCategory(1);

        List<Topic> topicList = creatAssociatedTopicList(size, TopicSide.TOPIC_A);

        for (Topic t : topicList) {
            setCategoryByReflection(category, t);
            setVoteCountByReflection(size--, t);
        }

        repository.saveAll(topicList);
        em.persist(member);
        em.persist(category);

        // when
        Slice<Topic> topics = repository.findAllNotHiddenByCategoryId(
                TopicStatus.VOTING,
                member.getId(),
                category.getId(),
                pageable
        );

        // then
        assertAll(
                () -> assertThat(topics.getSize()).isEqualTo(pageable.getPageSize()),
                () -> assertThat(topics.hasNext()).isFalse(),
                () -> assertThat(topics.getContent()).isEqualTo(topicList)
        );
    }

    private void setCategoryByReflection(Category category, Topic topic) throws NoSuchFieldException, IllegalAccessException {
        Field voteCountField = topic.getClass().getDeclaredField("category");
        voteCountField.setAccessible(true);
        voteCountField.set(topic, category);
    }

    private static void setVoteCountByReflection(int i, Topic topic) throws NoSuchFieldException, IllegalAccessException {
        Field voteCountField = topic.getClass().getDeclaredField("voteCount");
        voteCountField.setAccessible(true);
        voteCountField.set(topic, i);
    }
}