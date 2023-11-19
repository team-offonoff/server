package life.offonoff.ab.application.service.auth;

import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.util.jwt.token.JwtGenerator;
import life.offonoff.ab.util.password.PasswordEncoder;
import life.offonoff.ab.web.response.JoinStatusResponse;
import life.offonoff.ab.web.response.SignInResponse;
import life.offonoff.ab.web.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static life.offonoff.ab.domain.member.JoinStatus.*;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;

    private final JwtGenerator jwtGenerator;
    private final PasswordEncoder passwordEncoder;

    //== Sign Up ==//
    public SignUpResponse signUp(SignUpRequest request) {

        validateSignUp(request);

        Member saveMember = memberRepository.save(
                new Member(request.getEmail(), request.getPassword(), request.getProvider()));

        return new SignUpResponse(saveMember.getId(),
                                  AUTH_REGISTERED,
                                  jwtGenerator.generateAccessToken(saveMember.getId()));
    }

    private void validateSignUp(SignUpRequest request) {

        String email = request.getEmail();
        String rawPassword = request.getPassword();

        // encode password
        request.setEncodedPassword(passwordEncoder.encode(rawPassword));

        // unique email
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException();
        }
    }

    //== Sign In ==//
    public SignInResponse signIn(SignInRequest request) {

        validateSignIn(request);

        Member member = findMember(request.getEmail());

        return new SignInResponse(member.getId(),
                                  member.getJoinStatus(),
                                  jwtGenerator.generateAccessToken(member.getId()));
    }

    private void validateSignIn(SignInRequest request) {
        // email existence (non -> exception)
        Member member = findMember(request.getEmail());

        // match password
        if (!passwordEncoder.isMatch(request.getPassword(), member.getPassword())) {
            throw new IllegalPasswordException();
        }
    }

    public JoinStatusResponse getJoinStatus(Long memberId) {
        return new JoinStatusResponse(memberId, findMember(memberId).getJoinStatus());
    }

    private Member findMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));
    }
    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFountException(id));
    }
}
