package life.offonoff.ab.application.service.auth;

import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.QMember;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.util.password.PasswordEncoder;
import life.offonoff.ab.web.response.JoinStatusResponse;
import life.offonoff.ab.web.response.SignInResponse;
import life.offonoff.ab.web.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static life.offonoff.ab.domain.member.JoinStatus.*;
import static life.offonoff.ab.domain.member.QMember.member;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberService memberService;

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    //== Sign Up ==//
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {

        beforeSignUp(request);

        Member saveMember = memberService.join(request);

        return new SignUpResponse(saveMember.getId(),
                                  AUTH_REGISTERED,
                                  jwtProvider.generateAccessToken(saveMember.getId()));
    }

    private void beforeSignUp(SignUpRequest request) {

        String email = request.getEmail();

        if (memberService.exists(email)) {
            throw new DuplicateEmailException(email);
        }

        String rawPassword = request.getPassword();
        // encode password
        request.setEncodedPassword(passwordEncoder.encode(rawPassword));
    }

    //== Sign In ==//
    public SignInResponse signIn(SignInRequest request) {

        beforeSignIn(request);

        Member member = memberService.find(request.getEmail());

        return new SignInResponse(member.getId(),
                                  member.getJoinStatus(),
                                  jwtProvider.generateAccessToken(member.getId()));
    }

    private void beforeSignIn(SignInRequest request) {

        String email = request.getEmail();
        // email existence
        if (!memberService.exists(email)) {
            throw new MemberByEmailNotFountException(email);
        }

        // match password
        Member member = memberService.find(email);
        if (!passwordEncoder.isMatch(request.getPassword(), member.getPassword())) {
            throw new IllegalPasswordException();
        }
    }

    //== JoinStatus ==//
    public JoinStatusResponse getJoinStatus(Long memberId) {
        Member member = memberService.find(memberId);
        return new JoinStatusResponse(member.getId(), member.getJoinStatus());
    }
}
