package life.offonoff.ab.repository.member;

import jakarta.persistence.EntityManager;
import life.offonoff.ab.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
