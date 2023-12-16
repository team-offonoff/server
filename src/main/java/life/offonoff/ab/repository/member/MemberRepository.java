package life.offonoff.ab.repository.member;

import life.offonoff.ab.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findAllVotedTo(Long memberId);
    Optional<Member> findByIdAndActiveTrue(Long memberId);

    @Query("select m from Member m join fetch m.likedComments where m.id = :id")
    Optional<Member> findByIdFetchLikedComments(@Param("id") Long memberId);

    @Query("select m from Member m join fetch m.votes where m.id = :id")
    Optional<Member> findByIdFetchVotes(@Param("id") Long memberId);
}
