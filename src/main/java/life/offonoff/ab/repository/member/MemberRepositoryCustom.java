package life.offonoff.ab.repository.member;

import life.offonoff.ab.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    List<Member> findAllVotedTo(Long topicId);

    Optional<Member> findByEmail(String email);
}
