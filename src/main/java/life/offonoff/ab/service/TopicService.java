package life.offonoff.ab.service;

import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.NotificationEnabled;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.exception.CategoryNotFoundException;
import life.offonoff.ab.repository.CategoryRepository;
import life.offonoff.ab.repository.ChoiceRepository;
import life.offonoff.ab.repository.TopicRepository;
import life.offonoff.ab.service.request.ChoiceCreateRequest;
import life.offonoff.ab.service.request.TopicCreateRequest;
import life.offonoff.ab.web.response.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TopicService {
    private final CategoryRepository categoryRepository;
    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;

    @Transactional
    public TopicResponse createMembersTopic(final Long memberId, final TopicCreateRequest request) {
        Member member = findMember(memberId);
        Category category = findCategory(request.categoryId());
        Topic topic = new Topic(request.topicTitle(), request.topicSide(), request.deadline());
        topic.associate(member, category, null);
        topicRepository.save(topic);

        request.choices().stream()
                .map(choiceRequest -> createTopicsChoice(topic, choiceRequest))
                .forEach(choiceRepository::save);

        return TopicResponse.from(topic);
    }

    private Choice createTopicsChoice(final Topic topic, final ChoiceCreateRequest request) {
        ChoiceContent choiceContent = request.choiceContentRequest().toEntity();
        return new Choice(topic, request.choiceOption(), choiceContent);
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    // TODO: Find member
    private Member findMember(final Long memberId) {
        return new Member("name", "nickname", new NotificationEnabled(true, true, true, true));
    }

}
