package life.offonoff.ab.repository.keyword;

import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.topic.TopicSide;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static life.offonoff.ab.domain.TestEntityUtil.createPageableDesc;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class KeywordRepositoryTest {

    @Autowired
    KeywordRepository keywordRepository;

    @BeforeEach
    void beforeEach() {

    }

    @Test
    @DisplayName("키워드 Slice 조회, id 기반으로 정렬")
    void keyword_slice_sort_by_id_desc() {
        // given
        TopicSide side = TopicSide.TOPIC_A;
        Pageable pageable = createPageableDesc(0, 2, "id");

        Keyword keyword1 = new Keyword("key1", side);
        Keyword keyword2 = new Keyword("key2", side);

        keywordRepository.saveAll(List.of(keyword1, keyword2));

        // when
        Slice<Keyword> keywordSlice = keywordRepository.findAllByTopicSide(side, pageable);

        // then
        assertThat(keywordSlice.getContent()).containsExactlyElementsOf(List.of(keyword2, keyword1));
    }

}