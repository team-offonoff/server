package life.offonoff.ab.application.service.authenticate;

import life.offonoff.ab.application.service.request.SignInRequest;
import life.offonoff.ab.application.service.request.SignUpRequest;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.exception.DuplicateException;
import life.offonoff.ab.exception.EmailInvalidException;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.util.jwt.token.JwtGenerator;
import life.offonoff.ab.web.response.SignInResponse;
import life.offonoff.ab.web.response.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtGenerator jwtGenerator;

    //== Sign In ==//
    public SignInResponse signIn(SignInRequest request) {

        validateSignIn(request);

        Member member = findMember(request.getEmail());

        return new SignInResponse(jwtGenerator.generateAccessToken(member.getId()));
    }

    // TODO : 패스워드 로직 수정 (테스트도 추가)
    private void validateSignIn(SignInRequest request) {
        // email existence
        Member member = findMember(request.getEmail());

        // match password
        member.getPassword().equals(request.getPassword());
    }

    private Member findMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EmailInvalidException(email));
    }

    //== Sign Up ==//
    public SignUpResponse signUp(SignUpRequest request) {

        validateSignUp(request);

        Member saveMember = memberRepository.save(
                new Member(request.getEmail(), request.getPassword(), request.getProvider()));

        return new SignUpResponse(jwtGenerator.generateAccessToken(saveMember.getId()));
    }

    private void validateSignUp(SignUpRequest request) {

        String email = request.getEmail();

        // unique email
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new DuplicateException("duplicate email");
        }
    }
}
