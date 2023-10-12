package life.offonoff.ab.repository.member;

import life.offonoff.ab.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findAllVotedTo(Long aLong);
}
