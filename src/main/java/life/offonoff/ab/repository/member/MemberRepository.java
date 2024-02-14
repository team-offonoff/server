package life.offonoff.ab.repository.member;

import life.offonoff.ab.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findAllListeningVoteResultAndVotedTopicId(Long memberId);

    Optional<Member> findByIdAndActiveTrue(Long memberId);

    @Query("select m from Member m left join fetch m.likedComments where m.id = :id")
    Optional<Member> findByIdWithLikedComments(@Param("id") Long memberId);

    @Query("select exists (select 1 from Member m where m.personalInfo.nickname = :nickname)")
    boolean existsByNickname(String nickname);
}
