package life.offonoff.ab.repository.member;

import life.offonoff.ab.domain.member.Member;

import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findAllVotedTo(Long topicId);
}
