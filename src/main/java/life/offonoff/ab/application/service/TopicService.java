package life.offonoff.ab.application.service;

import life.offonoff.ab.application.event.report.TopicReportEvent;
import life.offonoff.ab.application.event.topic.TopicCreateEvent;
import life.offonoff.ab.application.service.request.*;
import life.offonoff.ab.domain.keyword.Keyword;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.topic.TopicSide;
import life.offonoff.ab.domain.topic.choice.Choice;
import life.offonoff.ab.domain.topic.choice.ChoiceOption;
import life.offonoff.ab.domain.topic.choice.content.ChoiceContent;
import life.offonoff.ab.domain.topic.hide.HiddenTopic;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.ChoiceRepository;
import life.offonoff.ab.repository.KeywordRepository;
import life.offonoff.ab.repository.VoteRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.topic.TopicResponse;
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
    private final VoteRepository voteRepository;

    private final ApplicationEventPublisher eventPublisher;

    //== Create ==//
    @Transactional
    public TopicResponse createMembersTopic(final Long memberId, final TopicCreateRequest request) {
        Member member = findMember(memberId);
        Keyword keyword = findOrCreateKeyword(request.keywordName(), request.side());
        LocalDateTime deadline = convertUnixTime(request.deadline());
        Topic topic = new Topic(member, keyword, request.title(), request.side(), deadline);
        topicRepository.save(topic);

        request.choices().stream()
                .map(choiceRequest -> createTopicsChoice(topic, choiceRequest))
                .forEach(choiceRepository::save);

        // topic 생성 이벤트 발행
        eventPublisher.publishEvent(new TopicCreateEvent(topic));
        return TopicResponse.from(topic);
    }

    @Transactional
    public void activateMembersTopic(final Long memberId, final Long topicId, final Boolean active) {
        Member member = findMember(memberId);
        Topic topic = findTopic(topicId);

        checkMemberCanTouchTopic(member, topic);

        boolean sameStatus = topic.isActive() == active;
        if (!sameStatus) {
            topic.activate(active);
        }
    }

    @Transactional
    public void deleteMembersTopic(final Long memberId, final Long topicId) {
        Member member = findMember(memberId);
        Topic topic = findTopic(topicId);

        checkMemberCanTouchTopic(member, topic);

        topicRepository.delete(topic);
    }

    private void checkMemberCanTouchTopic(Member member, Topic topic) {
        if (!member.isAdmin() && !topic.isWrittenBy(member)) {
            throw new IllegalTopicStatusChangeException(member.getId(), topic.getId());
        }
    }

    private Member findMember(final Long memberId) {
        return memberRepository.findByIdAndActiveTrue(memberId)
                               .orElseThrow(() -> new MemberByIdNotFoundException(memberId));
    }

    private Keyword findOrCreateKeyword(String keyword, TopicSide side) {
        return keywordRepository.findByNameAndSide(keyword, side)
                .orElseGet(() -> new Keyword(keyword, side));
    }

    private LocalDateTime convertUnixTime(Long unixTime) {
        return Instant.ofEpochSecond(unixTime)
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
    public Slice<TopicResponse> findAll(final Long memberId, final TopicSearchRequest request, final Pageable pageable) {
        return topicRepository.findAll(memberId, request, pageable)
                .map(TopicResponse::from);
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
        member.hideTopicIfNew(topic);
    }

    private void cancelHide(final Member member, final Topic topic) {
        member.cancelHideIfExists(topic);
    }

    //== Vote ==//
    @Transactional
    public void voteForTopicByMember(final Long topicId, final Long memberId, final VoteRequest request) {
        Member member = findMember(memberId);
        Topic topic = findTopic(topicId);
        final LocalDateTime votedAt = convertUnixTime(request.votedAt());

        checkTopicVotable(topic, member, votedAt);

        doVote(member, topic, votedAt, request.choiceOption());
    }

    private void doVote(final Member member, final Topic topic, final LocalDateTime votedAt, ChoiceOption choiceOption) {
        Vote vote = new Vote(choiceOption, votedAt);
        vote.associate(member, topic);
        voteRepository.save(vote);
    }

    private static void checkTopicVotable(final Topic topic, final Member member, final LocalDateTime votedAt) {
        if (!topic.isBeforeDeadline(votedAt)) {
            throw new UnableToVoteException(votedAt);
        }
        if (topic.isWrittenBy(member)) {
            throw new VoteByAuthorException(topic.getId(), member.getId());
        }
        final LocalDateTime now = LocalDateTime.now();
        boolean votedAtFuture = votedAt.isAfter(now);
        if (votedAtFuture) {
            throw new FutureTimeRequestException(votedAt, now);
        }
    }

    @Transactional
    public void cancelVoteForTopicByMember(final Long topicId, final Long memberId, final VoteCancelRequest request) {
        Member member = findMember(memberId);
        Topic topic = findTopic(topicId);
        final LocalDateTime votedAt = convertUnixTime(request.canceledAt());

        checkTopicVotable(topic, member, votedAt);

        Vote vote = findVoteByMemberIdAndTopicId(memberId, topicId);
        deleteVote(vote);
    }

    private void deleteVote(Vote vote) {
        vote.removeAssociations();
        voteRepository.delete(vote);
    }

    private Vote findVoteByMemberIdAndTopicId(Long memberId, Long topicId) {
        return voteRepository.findByVoterIdAndTopicId(memberId, topicId)
                .orElseThrow(() -> new MemberNotVoteException(memberId, topicId));
    }

    @Transactional
    public void reportTopicByMember(final Long topicId, final Long memberId) {
        final Member member = findMember(memberId);
        final Topic topic = findTopic(topicId);

        if (topic.isReportedBy(member)) {
            throw new TopicReportDuplicateException(topicId, memberId);
        }
        topic.reportBy(member);

        eventPublisher.publishEvent(
                new TopicReportEvent(TopicResponse.from(topic), topic.getReports().size()));
    }
}
