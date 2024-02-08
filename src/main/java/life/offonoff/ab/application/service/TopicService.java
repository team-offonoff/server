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
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.ChoiceRepository;
import life.offonoff.ab.repository.keyword.KeywordRepository;
import life.offonoff.ab.repository.VoteRepository;
import life.offonoff.ab.repository.comment.CommentRepository;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import life.offonoff.ab.web.response.ChoiceCountResponse;
import life.offonoff.ab.web.response.CommentResponse;
import life.offonoff.ab.web.response.VoteResponse;
import life.offonoff.ab.web.response.VoteResponseWithCount;
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
import java.util.List;

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
    private final CommentRepository commentRepository;

    private final ApplicationEventPublisher eventPublisher;

    //== Create ==//
    @Transactional
    public TopicResponse createMembersTopic(final Long memberId, final TopicCreateRequest request) {
        Member member = findMember(memberId);

        Topic topic = convertToTopic(member, request);
        topicRepository.save(topic);

        request.choices().stream()
                .map(choiceRequest -> createTopicsChoice(topic, choiceRequest))
                .forEach(choiceRepository::save);

        // topic 생성 이벤트 발행
        eventPublisher.publishEvent(new TopicCreateEvent(topic));
        return TopicResponse.from(topic);
    }

    private Topic convertToTopic(final Member member, final TopicCreateRequest request) {
        // A Side에선 마감시간과 키워드 없음
        boolean shouldHaveDeadlineAndKeyword = request.side() == TopicSide.TOPIC_B;
        if (shouldHaveDeadlineAndKeyword) {
            Keyword keyword = findOrCreateKeyword(request.keywordName(), request.side());
            LocalDateTime deadline = convertUnixTime(request.deadline());
            return new Topic(member, keyword, request.title(), request.side(), deadline);
        }
        return new Topic(member, request.title(), request.side());
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
     *
     * @param request  Specification을 이용해 검색 조건 추상화
     * @param pageable
     * @return
     */
    public Slice<TopicResponse> findAll(final Long memberId, final TopicSearchRequest request, final Pageable pageable) {

        Slice<Topic> topics = topicRepository.findAll(memberId, request, pageable);

        if (memberId == null) {
            return topics.map(TopicResponse::from);
        }

        return topics.map(topic -> TopicResponse.from(topic, findMember(memberId)));
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
    public VoteResponse voteForTopicByMember(final Long topicId, final Long memberId, final VoteRequest request) {
        Member member = findMember(memberId);
        Topic topic = findTopic(topicId);
        final LocalDateTime votedAt = convertUnixTime(request.votedAt());

        return voteForTopic(member, topic, votedAt, request.choiceOption());
    }

    private VoteResponse voteForTopic(final Member member, final Topic topic, final LocalDateTime votedAt, ChoiceOption choiceOption) {
        checkMemberVotableForTopic(member, topic, votedAt);

        Vote vote = new Vote(choiceOption, votedAt);
        vote.associate(member, topic);
        voteRepository.save(vote);

        if (topic.hasVoteResult()) {
            return VoteResponseWithCount.from(getLatestCommentOfTopic(topic), TopicResponse.from(topic, member), findChoiceCounts(topic.getId()));
        }
        return VoteResponse.from(getLatestCommentOfTopic(topic), TopicResponse.from(topic, member));
    }

    private CommentResponse getLatestCommentOfTopic(Topic topic) {
        return CommentResponse.from(
                commentRepository
                        .findFirstByTopicIdOrderByCreatedAtDesc(topic.getId())
                        .orElse(null)
        );
    }

    private void checkMemberVotableForTopic(final Member member, final Topic topic, final LocalDateTime votedAt) {
        checkTopicVotable(topic, votedAt);
        if (topic.isWrittenBy(member)) {
            throw new VoteByAuthorException(topic.getId(), member.getId());
        }
        if (member.votedAlready(topic)) {
            // 이미 투표했으면 또 투표 불가. 투표 다시하기 필요
            throw new AlreadyVotedException(topic.getId(), member.getVotedOptionOfTopic(topic));
        }
    }

    private void checkTopicVotable(final Topic topic, final LocalDateTime votedAt) {
        if (!topic.isBeforeDeadline(votedAt)) {
            throw new UnableToVoteException(votedAt);
        }
        final LocalDateTime now = LocalDateTime.now();
        boolean votedAtFuture = votedAt.isAfter(now);
        if (votedAtFuture) {
            throw new FutureTimeRequestException(votedAt, now);
        }
    }

    @Transactional
    public VoteResponse modifyVoteForTopicByMember(final Long topicId, final Long memberId, final VoteModifyRequest request) {
        final LocalDateTime modifiedAt = convertUnixTime(request.getModifiedAt());
        final ChoiceOption modifiedOption = request.getModifiedOption();

        Vote vote = findVoteByMemberIdAndTopicId(memberId, topicId);

        return modifyVote(vote, modifiedOption, modifiedAt);
    }

    private VoteResponse modifyVote(Vote vote, ChoiceOption modifiedOption, LocalDateTime modifiedAt) {
        checkVoteModifiable(vote, modifiedOption, modifiedAt);

        deleteVotersComments(vote.getVoter(), vote.getTopic());

        vote.changeOption(modifiedOption, modifiedAt);

        return VoteResponse.from(getLatestCommentOfTopic(vote.getTopic()), TopicResponse.from(vote.getTopic(), vote.getVoter()));
    }

    private void checkVoteModifiable(Vote vote, ChoiceOption modifiedOption, LocalDateTime modifiedAt) {
        checkTopicVotable(vote.getTopic(), modifiedAt);

        boolean optionVotedAlready = vote.isVotedForOption(modifiedOption);
        if (optionVotedAlready) {
            throw new DuplicateVoteOptionException(vote.getTopic().getId(), modifiedOption);
        }
    }

    private void deleteVotersComments(Member voter, Topic topic) {
        int deleted = commentRepository.deleteAllByWriterIdAndTopicId(voter.getId(), topic.getId());
        topic.commentRemoved(deleted);
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

    public List<ChoiceCountResponse> findChoiceCounts(Long topicId) {
        return voteRepository.findChoiceCountsByTopicId(topicId);
    }
}
