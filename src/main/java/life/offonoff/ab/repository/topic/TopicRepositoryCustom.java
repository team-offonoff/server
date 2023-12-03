package life.offonoff.ab.repository.topic;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface TopicRepositoryCustom {

//    Slice<Topic> findAll(TopicSearchRequest request, Pageable pageable);
    Slice<Topic> findAll(TopicSearchRequest request, Pageable pageable);
    List<Topic> findAll(TopicSearchCond cond);
}
