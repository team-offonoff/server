package life.offonoff.ab.repository.topic;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.repository.TestQueryDslConfig;
import life.offonoff.ab.service.request.TopicSearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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

        Member member = createMember(1);

        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            topics.add(TestTopic.builder()
                    .voteCount(size - i)
                    .publishMember(member)
                    .build()
                    .buildTopic()
            );
        }
        topicRepository.saveAll(topics);

        PageRequest pageable = PageRequest.of(0, size, Sort.Direction.DESC, "voteCount");
        TopicSearchRequest request = TopicSearchRequest.builder()
                                                       .topicStatus(TopicStatus.VOTING)
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
    @DisplayName("카테고리 ID로 토픽 Slice 조회")
    void 토픽_검색_Category() {
        // given
        int size = 5;

        Member member = createMember(1);
        Category category = createCategory(1);

        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            topics.add(TestTopic.builder()
                    .voteCount(size - i)
                    .category(category)
                    .publishMember(member)
                    .build()
                    .buildTopic()
            );
        }
        topicRepository.saveAll(topics);

        PageRequest pageable = PageRequest.of(0, size, Sort.Direction.DESC, "voteCount");
        TopicSearchRequest request = TopicSearchRequest.builder()
                .categoryId(category.getId())
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
}