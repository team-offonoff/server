package life.offonoff.ab.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.topic.TopicSide;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static life.offonoff.ab.domain.member.QMember.*;
import static life.offonoff.ab.domain.topic.TopicSide.TOPIC_B;
import static life.offonoff.ab.domain.vote.QVote.*;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findAllListeningVoteResultAndVotedTopicId(Long topicId) {
        return queryFactory
                .selectFrom(member)
                .join(vote)
                .on(member.id.eq(vote.voter.id)
                    .and(vote.topic.id.eq(topicId)
                         .and(vote.topic.side.eq(TOPIC_B)))
                ).where(member.notificationEnabled.voteResult.isTrue())
                .fetch();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.ofNullable(
                queryFactory
                        .select(member)
                        .from(member)
                        .where(member.authInfo.email.eq(email))
                        .fetchOne());
    }
}
