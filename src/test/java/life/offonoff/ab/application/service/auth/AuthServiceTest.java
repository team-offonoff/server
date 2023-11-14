package life.offonoff.ab.application.service.auth;

import life.offonoff.ab.application.service.request.auth.SignInRequest;
import life.offonoff.ab.application.service.request.auth.SignUpRequest;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.Provider;
import life.offonoff.ab.exception.DuplicateException;
import life.offonoff.ab.exception.EmailNotFoundException;
import life.offonoff.ab.repository.member.MemberRepository;
import life.offonoff.ab.util.jwt.token.JwtGenerator;
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
    MemberRepository memberRepository;
    @Mock
    JwtGenerator generator;

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

        // Member
        Member member = TestMember.builder()
                .id(1L)
                .email(email)
                .password(password)
                .build().buildMember();

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

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

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> authService.signIn(request)).isInstanceOf(EmailNotFoundException.class);
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
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

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

        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

        // when
        assertThatThrownBy(() -> authService.signUp(request)).isInstanceOf(DuplicateException.class);
    }
}