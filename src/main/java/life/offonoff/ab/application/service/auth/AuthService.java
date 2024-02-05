package life.offonoff.ab.application.service.auth;

import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.TermsRequest;
import life.offonoff.ab.application.service.request.auth.ProfileRegisterRequest;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.exception.DuplicateEmailException;
import life.offonoff.ab.exception.IllegalPasswordException;
import life.offonoff.ab.exception.MemberByEmailNotFoundException;
import life.offonoff.ab.util.password.PasswordEncoder;
import life.offonoff.ab.util.token.TokenProvider;
import life.offonoff.ab.web.response.TokenRequest;
import life.offonoff.ab.web.response.TokenResponse;
import life.offonoff.ab.web.response.auth.join.JoinStatusResponse;
import life.offonoff.ab.web.response.auth.join.JoinTermsResponse;
import life.offonoff.ab.web.response.auth.join.ProfileRegisterResponse;
import life.offonoff.ab.web.response.auth.join.SignUpResponse;
import life.offonoff.ab.web.response.auth.login.SignInResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static life.offonoff.ab.domain.member.JoinStatus.AUTH_REGISTERED;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    //== Sign Up ==//
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {

        beforeSignUp(request);

        Member saveMember = memberService.join(request);

        return new SignUpResponse(saveMember.getId(),
                                  AUTH_REGISTERED);
    }

    private void beforeSignUp(SignUpRequest request) {

        String email = request.getEmail();

        if (memberService.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }

        String rawPassword = request.getPassword();
        // encode password
        request.setEncodedPassword(passwordEncoder.encode(rawPassword));
    }

    //== JoinStatus ==//
    public JoinStatusResponse getJoinStatus(Long memberId) {
        Member member = memberService.findById(memberId);
        return new JoinStatusResponse(member.getId(), member.getJoinStatus());
    }

    @Transactional
    public JoinStatusResponse registerProfile(ProfileRegisterRequest request) {

        beforeRegisterProfile(request);

        Member member = memberService.findById(request.getMemberId());
        member.registerPersonalInfo(request.toPersonalInfo());

        return new ProfileRegisterResponse(member.getId(), member.getJoinStatus());
    }

    private void beforeRegisterProfile(ProfileRegisterRequest request) {
        final String nickname = request.getNickname();
        memberService.checkMembersNickname(nickname);

        final String job = request.getJob();
        memberService.checkMembersJob(job);
    }

    @Transactional
    public JoinStatusResponse registerTerms(TermsRequest request) {

        Member member = memberService.findById(request.getMemberId());
        member.agreeTerms(request.toTermsEnabled());
        // TODO : OCP에 맞게 설계 (정보 등록의 마지막 단계가 변할 때마다 코드 변경 불가피)
        return new JoinTermsResponse(member.getId(),
                                     member.getJoinStatus(),
                                     tokenProvider.generateAccessToken(member.getId()),
                                     tokenProvider.generateRefreshToken(member.getId()));
    }

    //== Sign In ==//
    public SignInResponse signIn(SignInRequest request) {

        beforeSignIn(request);

        Member member = memberService.findMember(request.getEmail());
        if (!member.joinCompleted()) {
            return new SignInResponse(member.getId(), member.getJoinStatus());
        }

        return new SignInResponse(member.getId(),
                                  member.getJoinStatus(),
                                  tokenProvider.generateAccessToken(member.getId()),
                                  tokenProvider.generateRefreshToken(member.getId()));
    }

    private void beforeSignIn(SignInRequest request) {
        String email = request.getEmail();
        Member member = memberService.findMember(email);

        // email existence
        if (!memberService.existsByEmail(email)) {
            throw new MemberByEmailNotFoundException(email);
        }

        // match password
        if (!passwordEncoder.isMatch(request.getPassword(), member.getPassword())) {
            throw new IllegalPasswordException();
        }
    }

    public TokenResponse getAuthTokens(TokenRequest request) {
        final Long memberId = findMemberOfToken(request.getRefreshToken()).getId();

        return TokenResponse.builder()
                            .memberId(memberId)
                            .accessToken(tokenProvider.generateAccessToken(memberId))
                            .refreshToken(tokenProvider.generateRefreshToken(memberId))
                            .build();
    }

    private Member findMemberOfToken(String refreshToken) {
        final Long parsedMemberId = tokenProvider.getMemberIdFromRefreshToken(refreshToken);

        return memberService.findMember(parsedMemberId);
    }
}
