package life.offonoff.ab.repository.keyword;

import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.topic.TopicSide;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface KeywordRepositoryCustom {

    Slice<Keyword> findAllByTopicSide(TopicSide side, Pageable pageable);
}
