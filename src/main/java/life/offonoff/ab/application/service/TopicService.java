package life.offonoff.ab.application.service;

import life.offonoff.ab.application.event.topic.TopicCreateEvent;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.exception.CategoryNotFoundException;
import life.offonoff.ab.repository.CategoryRepository;
import life.offonoff.ab.repository.ChoiceRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.application.service.request.ChoiceCreateRequest;
import life.offonoff.ab.application.service.request.TopicCreateRequest;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.web.response.TopicResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TopicService {
    private final CategoryRepository categoryRepository;
    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;
    private final MemberRepository memberRepository;

    private final ApplicationEventPublisher eventPublisher;

    //== Create ==//
    @Transactional
    public TopicResponse createMembersTopic(final Long memberId, final TopicCreateRequest request) {
        Member member = findMember(memberId);
        Category category = findCategory(request.categoryId());
        Topic topic = new Topic(member, category, request.topicTitle(), request.topicSide(), request.deadline());
        topicRepository.save(topic);

        request.choices().stream()
                .map(choiceRequest -> createTopicsChoice(topic, choiceRequest))
                .forEach(choiceRepository::save);

        // topic 생성 이벤트 발행
        eventPublisher.publishEvent(TopicCreateEvent.of(topic));
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
        return memberRepository.findById(memberId)
                               .orElseThrow();
    }

    //== Search ==//
    public Topic searchById(Long topicId) {
        return topicRepository.findById(topicId)
                              .orElseThrow();
    }

    /**
     * 토픽 검색 서비스
     * @param request Specification을 이용해 검색 조건 추상화
     * @param pageable
     * @return
     */
    public Slice<Topic> searchAll(final TopicSearchRequest request, final Pageable pageable) {
        return topicRepository.findAll(request, pageable);
    }

    //== Hide ==//
    @Transactional
    public void hide(Long memberId, Long topicId, Boolean hide) {
        Member member = findMember(memberId);
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
