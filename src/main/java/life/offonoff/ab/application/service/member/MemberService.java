package life.offonoff.ab.application.service.member;

import life.offonoff.ab.application.service.request.MemberRequest;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    //== join ==//
    @Transactional
    public Member join(final MemberRequest request) {
        return memberRepository.save(request.toMember());
    }

    //== find ==//
    public Member find(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberByIdNotFoundException(memberId)); // custom exception 추가 후 예외 핸들
    }

    public Member find(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberByEmailNotFoundException(email));
    }

    //== exists ==//
    public boolean exists(final Long memberId) {
        try {
            find(memberId);
        } catch (MemberNotFoundException notFountException) {
            return false;
        }
        return true;
    }

    public boolean exists(final String email) {
        try {
            find(email);
        } catch (MemberNotFoundException notFountException) {
            return false;
        }
        return true;
    }
}
