package life.offonoff.ab.application.service.auth;

import life.offonoff.ab.application.service.member.MemberService;
import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Provider;
import life.offonoff.ab.exception.DuplicateException;
import life.offonoff.ab.exception.EmailNotFoundException;
import life.offonoff.ab.exception.MemberByEmailNotFountException;
import life.offonoff.ab.exception.MemberNotFountException;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.util.token.JwtProvider;
import life.offonoff.ab.util.password.PasswordEncoder;
import life.offonoff.ab.web.response.SignInResponse;
import life.offonoff.ab.web.response.SignUpResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
    JwtProvider generator;
    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("정상 로그인")
    void sign_in_test() {
        // given
        Long id = 1L;
        String email = "email";
        String password = "password";
        String mockJwt = "jwt";

        SignInRequest request = new SignInRequest(email, password);

        when(generator.generateAccessToken(id)).thenReturn(mockJwt);
        when(memberService.exists(anyString())).thenReturn(true);
        when(passwordEncoder.isMatch(anyString(), anyString())).thenReturn(true);

        // Member
        Member member = TestMember.builder()
                .id(1L)
                .email(email)
                .password(password)
                .build().buildMember();

        when(memberService.find(anyString())).thenReturn(member);

        // when
        SignInResponse response = authService.signIn(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo(mockJwt);
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
        assertThatThrownBy(() -> authService.signIn(request)).isInstanceOf(MemberNotFountException.class);
    }

    @Test
    @DisplayName("정상 회원 가입")
    void sign_up_test() {
        // given
        Long id = 1L;
        String email = "email";
        String password = "password";
        String mockJwt = "jwt";

        // Member
        Member member = TestMember.builder()
                .id(id)
                .build().buildMember();
        SignUpRequest request = new SignUpRequest(email, password, Provider.NONE);

        when(generator.generateAccessToken(id)).thenReturn(mockJwt);
        when(memberService.join(any())).thenReturn(member);
        when(memberService.exists(anyString())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(password);

        // when
        SignUpResponse response = authService.signUp(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo(mockJwt);
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