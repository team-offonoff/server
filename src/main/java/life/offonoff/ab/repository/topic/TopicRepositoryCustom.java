package life.offonoff.ab.repository.topic;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.service.request.TopicSearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TopicRepositoryCustom {

    Slice<Topic> findAll(TopicSearchRequest request, Pageable pageable);
}
