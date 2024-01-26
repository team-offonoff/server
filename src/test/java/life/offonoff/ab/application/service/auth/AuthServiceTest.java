package life.offonoff.ab.application.service.auth;

import io.jsonwebtoken.ExpiredJwtException;
import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.auth.ProfileRegisterRequest;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.*;
import life.offonoff.ab.exception.*;
import life.offonoff.ab.exception.auth.token.ExpiredTokenException;
import life.offonoff.ab.exception.auth.token.InvalidSignatureTokenException;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.util.password.PasswordEncoder;
import life.offonoff.ab.web.TokenRequest;
import life.offonoff.ab.web.TokenResponse;
import life.offonoff.ab.web.response.auth.login.SignInResponse;
import life.offonoff.ab.web.response.auth.join.SignUpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SignatureException;
import java.time.LocalDate;

import static life.offonoff.ab.domain.TestEntityUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;
    @Mock
    MemberService memberService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtProvider jwtProvider;

    @Test
    @DisplayName("정상 로그인")
    void sign_in_test() {
        // given
        String email = "email";
        String password = "password";
        Member member = createCompletelyJoinedMember(email, password, "nickname");

        SignInRequest request = new SignInRequest(email, password);

        when(memberService.existsByEmail(anyString())).thenReturn(true);
        when(passwordEncoder.isMatch(anyString(), anyString())).thenReturn(true);
        when(memberService.findMember(anyString())).thenReturn(member);
        when(jwtProvider.generateAccessToken(nullable(Long.class))).thenReturn("access_token");

        // when
        SignInResponse response = authService.signIn(request);

        // then
        assertThat(response.getJoinStatus()).isEqualTo(JoinStatus.COMPLETE);
    }

    @Test
    @DisplayName("없는 이메일로 로그인 시 예외")
    void sign_in_exception_invalid_email() {
        // given
        String email = "email";
        String password = "password";

        SignInRequest request = new SignInRequest(email, password);

        when(memberService.existsByEmail(anyString())).thenReturn(false);

        // when
        assertThatThrownBy(() -> authService.signIn(request)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void signIn_deactivatedMember_exception() {
        // given
        String email = "email";
        String password = "password";

        SignInRequest request = new SignInRequest(email, password);

        when(memberService.findMember(anyString()))
                .thenThrow(MemberByEmailNotFoundException.class);

        // when
        assertThatThrownBy(() -> authService.signIn(request))
                .isInstanceOf(MemberByEmailNotFoundException.class);
    }

    @Test
    @DisplayName("정상 회원 가입")
    void sign_up_test() {
        // given
        Long id = 1L;
        String email = "email";
        String password = "password";

        // Member
        Member member = TestMember.builder()
                .id(id)
                .build().buildMember();
        SignUpRequest request = new SignUpRequest(email, password, Provider.NONE);

        when(memberService.join(any())).thenReturn(member);
        when(memberService.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(password);

        // when
        SignUpResponse response = authService.signUp(request);

        // then
        assertThat(response.getJoinStatus()).isEqualTo(JoinStatus.AUTH_REGISTERED);
    }

    @Test
    @DisplayName("중복 이메일로 회원가입시 예외")
    void sign_up_exception_invalid_email() {
        // given
        String email = "email";
        String password = "password";

        // Member
        Member member = TestMember.builder()
                .build().buildMember();

        SignUpRequest request = new SignUpRequest(email, password, Provider.NONE);

        when(memberService.existsByEmail(anyString())).thenReturn(true);

        // when
        assertThatThrownBy(() -> authService.signUp(request)).isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("중복 닉네임을 등록하게 되면 예외")
    void singup_personalInfo_exception_duplicate_nickname() {
        // given
        ProfileRegisterRequest registerRequest = new ProfileRegisterRequest(
                1L,
                "dup_nickname",
                LocalDate.now(),
                Gender.ETC,
                "job"
        );

        doThrow(DuplicateNicknameException.class)
                .when(memberService).checkMembersNickname(anyString());

        // then
        assertThatThrownBy(() -> authService.registerProfile(registerRequest))
                .isInstanceOf(DuplicateNicknameException.class);

    }

    @Test
    @DisplayName("refresh_token으로 새로운 refresh/access token 발급")
    void getAuthTokens() {
        // given
        Member member = TestMember.builder()
                                  .id(1L)
                                  .build().buildMember();

        TokenRequest request = new TokenRequest("old_refresh_token");

        when(jwtProvider.getMemberIdFromRefreshToken(anyString())).thenReturn(member.getId());
        when(memberService.findMember(anyLong())).thenReturn(member);

        // when
        TokenResponse tokenResponse = authService.getAuthTokens(request);

        // then
        assertThat(tokenResponse.getMemberId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("refresh_token의 member-id가 존재하지 않는 member-id면 예외")
    void getAuthTokens_member_not_found() {
        // given
        Member member = TestMember.builder()
                .id(1L)
                .build().buildMember();

        TokenRequest request = new TokenRequest("old_refresh_token");

        when(jwtProvider.getMemberIdFromRefreshToken(anyString())).thenReturn(member.getId());
        when(memberService.findMember(anyLong())).thenThrow(MemberByIdNotFoundException.class);

        // then
        assertThatThrownBy(() -> authService.getAuthTokens(request))
                .isInstanceOf(MemberByIdNotFoundException.class);
    }

    @Test
    @DisplayName("refresh_token의 member-id가 비활성화된 member면 예외")
    void getAuthTokens_member_deactivated() {
        // given
        Member member = TestMember.builder()
                .id(1L)
                .build().buildMember();

        TokenRequest request = new TokenRequest("old_refresh_token");

        when(jwtProvider.getMemberIdFromRefreshToken(anyString())).thenReturn(member.getId());
        when(memberService.findMember(anyLong())).thenThrow(MemberDeactivatedException.class);

        // then
        assertThatThrownBy(() -> authService.getAuthTokens(request))
                .isInstanceOf(MemberDeactivatedException.class);
    }

    @Test
    @DisplayName("만료된 refresh_token로 재발급 요청은 예외")
    void getAuthTokens_expired_refresh_token() {
        // given
        Member member = TestMember.builder()
                .id(1L)
                .build().buildMember();

        TokenRequest request = new TokenRequest("expired_refresh_token");

        when(jwtProvider.getMemberIdFromRefreshToken(anyString())).thenThrow(ExpiredTokenException.class);

        // then
        assertThatThrownBy(() -> authService.getAuthTokens(request))
                .isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    @DisplayName("다른 서명의 refresh_token로 재발급 요청은 예외")
    void getAuthTokens_invalid_refresh_token() {
        // given
        Member member = TestMember.builder()
                .id(1L)
                .build().buildMember();

        TokenRequest request = new TokenRequest("invalid_refresh_token");

        when(jwtProvider.getMemberIdFromRefreshToken(anyString())).thenThrow(InvalidSignatureTokenException.class);

        // then
        assertThatThrownBy(() -> authService.getAuthTokens(request))
                .isInstanceOf(InvalidSignatureTokenException.class);
    }
}