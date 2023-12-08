package life.offonoff.ab.repository.topic;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.repository.TestQueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(TestQueryDslConfig.class)
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
        System.out.println(topics1);

        PageRequest pageable = PageRequest.of(0, size, Sort.Direction.DESC, "voteCount");
        TopicSearchRequest request = TopicSearchRequest.builder()
                .topicStatus(TopicStatus.VOTING)
                .build();

        System.out.println(request.getTopicStatus());
        // when
        Slice<Topic> topicSlice = topicRepository.findAll(request, pageable);

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

        PageRequest pageable = PageRequest.of(0, size, Sort.Direction.DESC, "voteCount");
        TopicSearchRequest request = TopicSearchRequest.builder()
                .keywordId(keyword.getId())
                .build();
        // when
        Slice<Topic> topicSlice = topicRepository.findAll(request, pageable);

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
}