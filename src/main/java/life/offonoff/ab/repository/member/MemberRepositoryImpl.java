package life.offonoff.ab.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import life.offonoff.ab.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static life.offonoff.ab.domain.member.QMember.*;
import static life.offonoff.ab.domain.vote.QVote.*;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findAllVotedTo(Long topicId) {
        return queryFactory
                .selectFrom(member)
                .join(vote)
                .on(
                        member.id.eq(vote.member.id)
                        .and(vote.topic.id.eq(topicId))
                ).where(member.notificationEnabled.votingResult.isTrue())
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
