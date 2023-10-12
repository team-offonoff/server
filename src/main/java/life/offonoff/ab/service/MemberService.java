package life.offonoff.ab.service;

import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public Member searchById(Long memberId) {
        return memberRepository.findById(memberId)
                               .orElseThrow(); // custom exception 추가 후 예외 핸들
    }
}
