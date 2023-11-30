package life.offonoff.ab.application.service;

import life.offonoff.ab.application.event.topic.TopicCreateEvent;
import life.offonoff.ab.application.service.request.ChoiceCreateRequest;
import life.offonoff.ab.application.service.request.TopicCreateRequest;
import life.offonoff.ab.application.service.request.TopicSearchRequest;
import life.offonoff.ab.application.service.request.VoteRequest;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.KeywordRepository;
import life.offonoff.ab.repository.ChoiceRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.TopicResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TopicService {
    private final KeywordRepository keywordRepository;
    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;
    private final MemberRepository memberRepository;

    private final ApplicationEventPublisher eventPublisher;

    //== Create ==//
    @Transactional
    public TopicResponse createMembersTopic(final Long memberId, final TopicCreateRequest request) {
        Member member = findMember(memberId);
        Keyword keyword = findOrCreateKeyword(request.keywordName(), request.side());
        LocalDateTime deadline = convertTime(request.deadline());
        Topic topic = new Topic(member, keyword, request.title(), request.side(), deadline);
        topicRepository.save(topic);

        request.choices().stream()
                .map(choiceRequest -> createTopicsChoice(topic, choiceRequest))
                .forEach(choiceRepository::save);

        // topic 생성 이벤트 발행
        eventPublisher.publishEvent(new TopicCreateEvent(topic));
        return TopicResponse.from(topic);
    }

    private Member findMember(final Long memberId) {
        return memberRepository.findByIdAndActiveTrue(memberId)
                               .orElseThrow(() -> new MemberByIdNotFoundException(memberId));
    }

    private Keyword findOrCreateKeyword(String keyword, TopicSide side) {
        return keywordRepository.findByNameAndSide(keyword, side)
                .orElseGet(() -> new Keyword(keyword, side));
    }

    private LocalDateTime convertTime(Long deadline) {
        return Instant.ofEpochSecond(deadline)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private Choice createTopicsChoice(final Topic topic, final ChoiceCreateRequest request) {
        ChoiceContent choiceContent = request.choiceContentRequest().toEntity();
        return new Choice(topic, request.choiceOption(), choiceContent);
    }

    //== Search ==//
    public Topic findTopic(final Long topicId) {
        return topicRepository.findByIdAndActiveTrue(topicId)
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
    public void hideTopicForMember(final Long memberId, final Long topicId, final Boolean hide) {
        Member member = findMember(memberId);
        Topic topic = this.findTopic(topicId);

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
        Topic topic = findTopic(topicId);

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
