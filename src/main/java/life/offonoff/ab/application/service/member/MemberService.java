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
    public Member findById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberByIdNotFoundException(memberId)); // custom exception 추가 후 예외 핸들
    }

    public Member findByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberByEmailNotFoundException(email));
    }

    //== exists ==//
    public boolean exists(final Long memberId) {
        try {
<<<<<<< Updated upstream
            find(memberId);
        } catch (MemberNotFoundException notFountException) {
=======
            findById(memberId);
        } catch (MemberNotFountException notFountException) {
>>>>>>> Stashed changes
            return false;
        }
        return true;
    }

    public boolean exists(final String email) {
        try {
<<<<<<< Updated upstream
            find(email);
        } catch (MemberNotFoundException notFountException) {
=======
            findByEmail(email);
        } catch (MemberNotFountException notFountException) {
>>>>>>> Stashed changes
            return false;
        }
        return true;
    }
}
