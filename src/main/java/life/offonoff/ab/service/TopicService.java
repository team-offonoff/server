package life.offonoff.ab.service;

import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.NotificationEnabled;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicStatus;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.exception.CategoryNotFoundException;
import life.offonoff.ab.repository.CategoryRepository;
import life.offonoff.ab.repository.ChoiceRepository;
import life.offonoff.ab.repository.TopicRepository;
import life.offonoff.ab.service.dto.TopicSearchParams;
import life.offonoff.ab.service.request.ChoiceCreateRequest;
import life.offonoff.ab.service.request.TopicCreateRequest;
import life.offonoff.ab.web.response.TopicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TopicService {
    private final CategoryRepository categoryRepository;
    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;
    private final MemberService memberService;

    @Transactional
    public TopicResponse createMembersTopic(final Long memberId, final TopicCreateRequest request) {
        Member member = findMember(memberId);
        Category category = findCategory(request.categoryId());
        Topic topic = new Topic(member, category, request.topicTitle(), request.topicSide(), request.deadline());
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

    public Topic searchById(Long topicId) {
        return topicRepository.findById(topicId)
                              .orElseThrow();
    }

    public Slice<Topic> searchAllNotHidden(TopicSearchParams params, Pageable pageable) {
        TopicStatus topicStatus = params.getTopicStatus();
        Long memberId = params.getMemberId();
        Long categoryId = params.getCategoryId();

        if (categoryId == null) {
            return topicRepository.findAllNotHidden(topicStatus, memberId, pageable);
        }
        return topicRepository.findAllNotHiddenByCategoryId(topicStatus, memberId, categoryId, pageable);
    }

    @Transactional
    public void hide(Long memberId, Long topicId, Boolean hide) {
        Member member = memberService.searchById(memberId);
        Topic topic = this.searchById(topicId);

        if (hide) {
            doHide(member, topic);
            return;
        }
        cancelHide(member, topic);
    }

    private void doHide(Member member, Topic topic) {
        if (!member.hideAlready(topic)) {
            HiddenTopic hiddenTopic = new HiddenTopic();
            hiddenTopic.associate(member, topic);
        }
    }

    private void cancelHide(Member member, Topic topic) {
        if (member.hideAlready(topic)) {
            topic.removeHiddenBy(member);
        }
    }
}