package life.offonoff.ab.repository.topic.specification;

import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import org.springframework.data.jpa.domain.Specification;

public class TopicSpecificationFactory {

    public static Specification<Topic> create(SpecificationRequest request) {
        Specification<Topic> spec = (t, query, cb) -> null;

        if (request instanceof TopicSearchRequest searchRequest) {

            if (searchRequest.getTopicStatus() != null) {
                spec = spec.and(TopicSpecifications.status(searchRequest.getTopicStatus()));
            }
            if (searchRequest.getCategoryId() != null) {
                spec = spec.and(TopicSpecifications.category(searchRequest.getCategoryId()));
            }
            if (searchRequest.getHidden() != null) {
                spec = spec.and(TopicSpecifications.hiddenOrNotByMember(searchRequest.getHidden(), searchRequest.getMemberId()));
            }
            return spec;
        }
        return spec;
    }

    public interface SpecificationRequest {
    }
}
