package life.offonoff.ab.application.service.auth;

import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.*;
import life.offonoff.ab.exception.DuplicateException;
import life.offonoff.ab.exception.MemberNotFoundException;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.util.password.PasswordEncoder;
import life.offonoff.ab.web.response.auth.login.SignInResponse;
import life.offonoff.ab.web.response.auth.join.SignUpResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        when(memberService.exists(anyString())).thenReturn(true);
        when(passwordEncoder.isMatch(anyString(), anyString())).thenReturn(true);
        when(memberService.find(anyString())).thenReturn(member);
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

        when(memberService.exists(anyString())).thenReturn(false);

        // when
        assertThatThrownBy(() -> authService.signIn(request)).isInstanceOf(MemberNotFoundException.class);
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
        when(memberService.exists(anyString())).thenReturn(false);
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

        when(memberService.exists(anyString())).thenReturn(true);

        // when
        assertThatThrownBy(() -> authService.signUp(request)).isInstanceOf(DuplicateException.class);
    }
}