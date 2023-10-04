package life.offonoff.ab.repository.specification;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.repository.TopicRepository;
import life.offonoff.ab.service.request.TopicSearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static life.offonoff.ab.repository.specification.TopicSpecifications.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TopicSpecificationsTest {

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("Category Specification 조회")
    void CategoryId로_Topic_조회() {
        // given
        Member member = createMember(1);
        Category category = createCategory(1);
        Topic topic = createTopic(1, TopicSide.TOPIC_A);
        topic.associate(member, category, null);

        topicRepository.save(topic);

        // when
        Specification<Topic> equalCategory = category(category.getId());
        List<Topic> topics = topicRepository.findAll(equalCategory);

        // then
        assertAll(
                () -> assertThat(topics.size()).isEqualTo(1),
                () -> assertThat(topics).containsExactly(topic)
        );
    }

    @Test
    @DisplayName("한 개 이상의 Specification으로 Topic조회 (TopicStatus/Category) ")
    void TopicStatus_AND_Category() {
        // given
        Member member = createMember(1);
        Category category = createCategory(1);

        Topic topic1 = createTopic(1, TopicSide.TOPIC_A);
        topic1.associate(member, category, null);

        Topic topic2 = createTopic(1, TopicSide.TOPIC_A);
        topic2.associate(member, category, null);
        topic2.endVote();

        topicRepository.save(topic1);
        topicRepository.save(topic2);

        // when
        Specification<Topic> categorySpec = category(category.getId());
        Specification<Topic> status = status(TopicStatus.VOTING);

        Specification<Topic> statusAndCategorySpec = status.and(categorySpec);
        List<Topic> topics = topicRepository.findAll(statusAndCategorySpec);

        // then
        assertAll(
                () -> assertThat(topics.size()).isEqualTo(1),
                () -> assertThat(topics).containsExactly(topic1)
        );
    }

    @Test
    @DisplayName("Hide 되지 않은 토픽은 조회")
    void Topic_조회_Open_To_A_Member() {
        // given
        Member anyMember = createMember(0);

        Member publishMember = createMember(1);
        Category category = createCategory(1);
        Topic topic = createTopic(1, TopicSide.TOPIC_A);
        topic.associate(publishMember, category, null);

        topicRepository.save(topic);
        em.persist(anyMember);

        // when
        List<Topic> topics = topicRepository.findAll(hiddenOrNotByMember(false, anyMember.getId()));

        // then
        assertAll(
                () -> assertThat(topics.size()).isEqualTo(1),
                () -> assertThat(anyMember.hideAlready(topic)).isFalse()
        );
    }

    @Test
    @DisplayName("Hidden 토픽 조회")
    void Hidden_Topic_조회() {
        // given
        Member anyMember = createMember(0);
        em.persist(anyMember);

        Member publishMember = createMember(1);
        Category category = createCategory(1);
        Topic topic = createTopic(1, TopicSide.TOPIC_A);
        topic.associate(publishMember, category, null);

        HiddenTopic hiddenTopic = new HiddenTopic();
        hiddenTopic.associate(anyMember, topic);

        topicRepository.save(topic);

        // when
        Specification<Topic> spec = hiddenOrNotByMember(true, anyMember.getId());
        List<Topic> topics = topicRepository.findAll(spec);

        // then
        assertThat(topics.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Voting + Open 토픽 조회")
    void Topic_조회_Voting_Open() {
        // given
        Member anyMember = createMember(0);

        Member publishMember = createMember(1);
        Category category = createCategory(1);
        Topic topic = createTopic(1, TopicSide.TOPIC_A);
        topic.associate(publishMember, category, null);

        topicRepository.save(topic);
        em.persist(anyMember);

        // when
        Specification<Topic> votingAndOpen =
                status(TopicStatus.VOTING).and(hiddenOrNotByMember(false, anyMember.getId()));

        List<Topic> topics = topicRepository.findAll(votingAndOpen);

        // then
        assertAll(
                () -> assertThat(topics.size()).isEqualTo(1),
                () -> assertThat(anyMember.hideAlready(topic)).isFalse()
        );
    }

    @Test
    @DisplayName("Voting + Open + Category 토픽 조회")
    void Topic_조회_Voting_Open_Category() {
        // given
        Member anyMember = createMember(0);

        Member publishMember = createMember(1);
        Category category = createCategory(1);
        Topic topic = createTopic(1, TopicSide.TOPIC_A);
        topic.associate(publishMember, category, null);

        topicRepository.save(topic);
        em.persist(anyMember);

        // when
        Specification<Topic> votingAndOpen = status(TopicStatus.VOTING).and(category(category.getId()))
                                                                       .and(hiddenOrNotByMember(false, anyMember.getId()));
        List<Topic> topics = topicRepository.findAll(votingAndOpen);

        // then
        assertAll(
                () -> assertThat(topics.size()).isEqualTo(1),
                () -> assertThat(anyMember.hideAlready(topic)).isFalse()
        );
    }

    @Test
    void SpecificationFactory로_Specification_생성() {
        // given
        TopicSearchRequest searchRequest = createSearchRequest();
        Specification<Topic> spec = TopicSpecificationFactory.create(searchRequest);


        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "voteCount");
        topicRepository.findAll(spec, pageable);
        // when

        // then

    }

    private static TopicSearchRequest createSearchRequest() {
        TopicSearchRequest searchRequest = new TopicSearchRequest();
        searchRequest.setTopicStatus(TopicStatus.VOTING);
        searchRequest.setHidden(false);
        searchRequest.setMemberId(1L);

        return searchRequest;
    }
}