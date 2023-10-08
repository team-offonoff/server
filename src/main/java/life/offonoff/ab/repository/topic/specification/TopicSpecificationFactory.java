package life.offonoff.ab.repository.specification;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.service.request.TopicSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import static life.offonoff.ab.repository.specification.TopicSpecifications.*;

public class TopicSpecificationFactory {

    public static Specification<Topic> create(SpecificationRequest request) {
        Specification<Topic> spec = (t, query, cb) -> null;

        if (request instanceof TopicSearchRequest searchRequest) {

            if (searchRequest.getTopicStatus() != null) {
                spec = spec.and(status(searchRequest.getTopicStatus()));
            }
            if (searchRequest.getCategoryId() != null) {
                spec = spec.and(category(searchRequest.getCategoryId()));
            }
            if (searchRequest.getHidden() != null) {
                spec = spec.and(hiddenOrNotByMember(searchRequest.getHidden(), searchRequest.getMemberId()));
            }
            return spec;
        }
        return spec;
    }

    public interface SpecificationRequest {
    }
}
