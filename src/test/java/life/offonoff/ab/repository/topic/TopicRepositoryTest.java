package life.offonoff.ab.repository.topic;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.configuration.TestJPAConfig;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(TestJPAConfig.class)
@DataJpaTest
class TopicRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    TopicRepository topicRepository;

    @Test
    @DisplayName("TopicStatus로 토픽 Slice 조회")
    void Voting_토픽_검색() {
        // given
        int size = 5;

        Member member = createMember("email", "password");
        em.persist(member);

        Keyword keyword = createKeyword(1);

        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Topic topic = TestTopic.builder()
                    .voteCount(size - i)
                    .keyword(keyword)
                    .author(member)
                    .build()
                    .buildTopic();

            topics.add(topic);
        }
        topicRepository.saveAll(topics);

        List<Topic> topics1 = topicRepository.findAll();

        TopicSearchRequest request = TopicSearchRequest.builder()
                .status(TopicStatus.VOTING)
                .build();

        // when
        Slice<Topic> topicSlice = topicRepository.findAll(member.getId(), request, createVoteCountDescPageable(0, size));

        // then
        assertAll(
                () -> assertThat(topicSlice.isEmpty()).isFalse(),
                () -> assertThat(topicSlice.getSize()).isEqualTo(size),
                () -> assertThat(topicSlice.hasNext()).isFalse(),
                () -> assertThat(topicSlice.getContent()).containsExactlyElementsOf(topics),
                () -> assertThat(topicSlice.getContent()).isSortedAccordingTo((t1, t2) -> t2.getVoteCount() - t1.getVoteCount())
        );
    }

    @Test
    @DisplayName("카테고리 ID로 토픽 Slice 조회")
    void 토픽_검색_Keyword() {
        // given
        int size = 5;

        Member member = createMember("email", "password");
        em.persist(member);

        Keyword keyword = createKeyword(1);

        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            topics.add(TestTopic.builder()
                    .voteCount(size - i)
                    .keyword(keyword)
                    .author(member)
                    .build()
                    .buildTopic()
            );
        }
        topicRepository.saveAll(topics);

        TopicSearchRequest request = TopicSearchRequest.builder()
                .keywordId(keyword.getId())
                .build();
        // when
        Slice<Topic> topicSlice = topicRepository.findAll(member.getId(), request, createVoteCountDescPageable(0, size));

        // then
        assertAll(
                () -> assertThat(topicSlice.isEmpty()).isFalse(),
                () -> assertThat(topicSlice.getSize()).isEqualTo(size),
                () -> assertThat(topicSlice.hasNext()).isFalse(),
                () -> assertThat(topicSlice.getContent()).containsExactlyElementsOf(topics),
                () -> assertThat(topicSlice.getContent()).isSortedAccordingTo((t1, t2) -> t2.getVoteCount() - t1.getVoteCount())
        );
    }

    @Test
    @DisplayName("TopicSide로 A토픽 Slice 조회")
    void 토픽_검색_sideA() {
        // given
        // Member
        Member member = createMember("email", "password");
        em.persist(member);

        // Topic
        Topic topicA = TestTopic.builder()
                .voteCount(10)
                .author(member)
                .side(TopicSide.TOPIC_A)
                .build().buildTopic();

        topicRepository.save(topicA);

        TopicSearchRequest request = TopicSearchRequest.builder()
                .side(topicA.getSide())
                .build();
        // when
        Slice<Topic> topicSlice = topicRepository.findAll(member.getId(), request, createVoteCountDescPageable(0, 1));

        // then
        assertAll(
                () -> assertThat(topicSlice.isEmpty()).isFalse(),
                () -> assertThat(topicSlice.getContent()).containsExactlyElementsOf(List.of(topicA))
                );
    }

    @Test
    @DisplayName("TopicSide로 B토픽 Slice 조회")
    void 토픽_검색_sideB() {
        // given
        // Member
        Member member = createMember("email", "password");
        em.persist(member);

        // Keyword
        Keyword keyword = createKeyword(1);

        // Topic
        Topic topicB = TestTopic.builder()
                .voteCount(10)
                .keyword(keyword)
                .author(member)
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();

        topicRepository.save(topicB);

        TopicSearchRequest request = TopicSearchRequest.builder()
                .side(topicB.getSide())
                .build();
        // when
        Slice<Topic> topicSlice = topicRepository.findAll(member.getId(), request, createVoteCountDescPageable(0, 1));

        // then
        assertAll(
                () -> assertThat(topicSlice.isEmpty()).isFalse(),
                () -> assertThat(topicSlice.getContent()).containsExactlyElementsOf(List.of(topicB))
        );
    }

    @Test
    @DisplayName("CLOSED 토픽 Slice 조회")
    void 토픽_검색_CLOSED() {
        // given
        // Member
        Member member = createMember("email", "password");
        em.persist(member);

        // Keyword
        Keyword keyword = createKeyword(1);

        // Topic
        Topic topicB = TestTopic.builder()
                .voteCount(10)
                .keyword(keyword)
                .author(member)
                .side(TopicSide.TOPIC_B)
                .status(TopicStatus.CLOSED)
                .build().buildTopic();

        topicRepository.save(topicB);

        TopicSearchRequest request = TopicSearchRequest.builder()
                .side(topicB.getSide())
                .status(TopicStatus.CLOSED)
                .build();
        // when
        Slice<Topic> topicSlice = topicRepository.findAll(member.getId(), request, createVoteCountDescPageable(0, 1));

        // then
        assertAll(
                () -> assertThat(topicSlice.isEmpty()).isFalse(),
                () -> assertThat(topicSlice.getContent()).containsExactlyElementsOf(List.of(topicB))
        );
    }
    @Test
    @DisplayName("")
    void findBy_TopicSearchCond() {
        // given

        // create Topic
        LocalDateTime deadline = LocalDateTime.now();

        Topic topic = TestTopic.builder()
                .deadline(deadline)
                .status(TopicStatus.VOTING)
                .build().buildTopic();

        em.persist(topic);

        TopicSearchCond searchCond = new TopicSearchCond(null, null, TopicStatus.VOTING);

        // when
        List<Topic> topics = topicRepository.findAll(searchCond);

        // then
        assertThat(topics).containsExactly(topic);
    }

    @Test
    @DisplayName("member가 조회 시에 hide한 토픽은 제외")
    void find_except_hidden() {
        // given

          // create Member
        Member author = createMember("author", "author");
        em.persist(author);

        Member member = createMember("email", "password");
        em.persist(member);

          // create Keyword
        Keyword keyword = createKeyword(1);
        em.persist(keyword);

          // create Topic
        LocalDateTime deadline = LocalDateTime.now();

        Topic topic = TestTopic.builder()
                .deadline(deadline)
                .author(author)
                .keyword(keyword)
                .status(TopicStatus.VOTING)
                .build().buildTopic();
        em.persist(topic);

        Topic topicHided = TestTopic.builder()
                .deadline(deadline)
                .author(author)
                .keyword(keyword)
                .status(TopicStatus.VOTING)
                .build().buildTopic();
        em.persist(topicHided);

          // hide
        HiddenTopic hiddenTopic = new HiddenTopic();
        hiddenTopic.associate(member, topicHided);
        em.persist(hiddenTopic);

        // when
        Slice<Topic> topics = topicRepository.findAll(member.getId(),
                                                      TopicSearchRequest.builder().build(),
                                                      createVoteCountDescPageable(0, 2));

        // then
        assertThat(topics.getContent()).containsExactly(topic);
    }

    @Test
    void delete_topic() {
        // given
        Member author = TestMember.builder()
                .build().buildMember();
        em.persist(author);

        Topic topic = TestTopic.builder()
                .author(author)
                .side(TopicSide.TOPIC_B)
                .build().buildTopic();

        topicRepository.save(topic);

        // when
        topicRepository.delete(topic);

        // then
        assertThat(topicRepository.findById(topic.getId())).isEmpty();
    }

    Pageable createVoteCountDescPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.Direction.DESC, "voteCount");
    }
}