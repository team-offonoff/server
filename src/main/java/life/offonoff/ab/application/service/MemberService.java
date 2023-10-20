package life.offonoff.ab.application.service;

import life.offonoff.ab.application.service.request.VoteRequest;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.Topic;
import life.offonoff.ab.domain.vote.Vote;
import life.offonoff.ab.exception.MemberNotFountException;
import life.offonoff.ab.exception.TopicNotFoundException;
import life.offonoff.ab.exception.UnableToProcessException;
import life.offonoff.ab.exception.UnableToVoteException;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TopicRepository topicRepository;

    //== Search ==//
    public Member searchById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFountException(memberId)); // custom exception 추가 후 예외 핸들
    }

    //== Vote ==//
    @Transactional
    public void vote(final Long memberId, final VoteRequest request) {
        Member member = searchById(memberId);
        Topic topic = findTopic(request.topicId());

        validateVotable(topic, request);

        Vote vote = new Vote(request.choiceOption());
        vote.associate(member, topic);
    }

    private static void validateVotable(final Topic topic, final VoteRequest request) {
        if (!topic.votable(request.requestTime())) {
            throw new UnableToVoteException(request.requestTime());
        }
    }

    private Topic findTopic(final Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new TopicNotFoundException(topicId));
    }
}
