package life.offonoff.ab.application.service;

import life.offonoff.ab.application.event.topic.TopicCreateEvent;
import life.offonoff.ab.application.service.request.VoteRequest;
import life.offonoff.ab.domain.category.Category;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.CategoryNotFoundException;
import life.offonoff.ab.exception.MemberNotFountException;
import life.offonoff.ab.exception.TopicNotFoundException;
import life.offonoff.ab.exception.UnableToVoteException;
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

    private Category findCategory(final Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    // TODO: Find member
    private Member findMember(final Long memberId) {
        return memberRepository.findById(memberId)
                               .orElseThrow(() -> new MemberNotFountException(memberId));
    }

    //== Search ==//
    public Topic searchById(final Long topicId) {
        return topicRepository.findById(topicId)
                              .orElseThrow(() -> new TopicNotFoundException(topicId));
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
    public void hide(final Long memberId, final Long topicId, final Boolean hide) {
        Member member = findMember(memberId);
        Topic topic = this.searchById(topicId);

        if (hide) {
            doHide(member, topic);
            return;
        }
        cancelHide(member, topic);
    }

    private void doHide(final Member member, final Topic topic) {
        if (!member.hideAlready(topic)) {
            HiddenTopic hiddenTopic = new HiddenTopic();
            hiddenTopic.associate(member, topic);
        }
    }

    private void cancelHide(final Member member, final Topic topic) {
        if (member.hideAlready(topic)) {
            member.cancelHide(topic);
        }
    }

    //== Vote ==//
    @Transactional
    public void vote(final Long topicId, final VoteRequest request) {
        Member member = findMember(request.memberId());
        Topic topic = searchById(topicId);

        validateVotable(topic, request);

        if (request.vote()) {
            doVote(member, topic, request);
            return;
        }
        cancelVote(member, topic);
    }

    private void doVote(final Member member, final Topic topic, VoteRequest request) {
        Vote vote = new Vote(request.choiceOption());
        vote.associate(member, topic);
    }

    private void cancelVote(final Member member, final Topic topic) {
        if (member.votedAlready(topic)) {
            member.cancelVote(topic);
        }
    }

    private static void validateVotable(final Topic topic, final VoteRequest request) {
        if (!topic.votable(request.requestTime())) {
            throw new UnableToVoteException(request.requestTime());
        }
    }
}
