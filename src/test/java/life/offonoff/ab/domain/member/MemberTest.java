package life.offonoff.ab.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MemberTest {

    @Test
    @DisplayName("AuthInfo 중복 등록시 예외X 그냥 무시")
    void register_authInfo_exception() {
        // given
        String email = "email";
        String password = "password";
        Provider provider = Provider.NONE;
        AuthenticationInfo authInfo = new AuthenticationInfo(email, password, provider);
        Member member = new Member(email, password, provider);

        // when
        Executable code = () -> member.registerAuthInfo(authInfo);

        // then
        assertDoesNotThrow(code);
    }

    @Test
    @DisplayName("PersonalInfo 등록")
    void register_personalInfo_success() {
        // given
        Member member = new Member("email", "password", Provider.NONE);
        PersonalInfo personalInfo = new PersonalInfo("nickname", LocalDate.now(), Gender.ETC, "job");

        // when
        member.registerPersonalInfo(personalInfo);

        // then
        assertAll(
                () -> assertThat(member.getNickname()).isEqualTo(personalInfo.getNickname()),
                () -> assertThat(member.getJoinStatus()).isEqualTo(JoinStatus.PERSONAL_REGISTERED)
        );
    }

    @Test
    @DisplayName("PersonalInfo 중복 등록시 예외X 그냥 무시")
    void register_personalInfo_exception() {
        // given
        Member member = new Member("email", "password", Provider.NONE);
        PersonalInfo personalInfo = new PersonalInfo("nickname", LocalDate.now(), Gender.ETC, "job");
        member.registerPersonalInfo(personalInfo);

        // when
        Executable code = () -> member.registerPersonalInfo(personalInfo);

        // then
        assertDoesNotThrow(code);
    }

    @Test
    @DisplayName("TermsEnabled 등록")
    void register_termsEnabled_success() {
        // given
        Member member = new Member("email", "password", Provider.NONE);
        PersonalInfo personalInfo = new PersonalInfo("nickname", LocalDate.now(), Gender.ETC, "job");
        member.registerPersonalInfo(personalInfo);

        TermsEnabled termsEnabled = new TermsEnabled(true);

        // when
        member.agreeTerms(termsEnabled);

        // then
        assertThat(member.getJoinStatus()).isEqualTo(JoinStatus.COMPLETE);
    }

    @Test
    @DisplayName("TermsEnabled 중복 등록시 예외X 그냥 무시")
    void register_termsEnabled_exception() {
        // given
        Member member = new Member("email", "password", Provider.NONE);
        PersonalInfo personalInfo = new PersonalInfo("nickname", LocalDate.now(), Gender.ETC, "job");
        member.registerPersonalInfo(personalInfo);

        TermsEnabled termsEnabled = new TermsEnabled(true);
        member.agreeTerms(termsEnabled);

        // when
        Executable code = () -> member.agreeTerms(termsEnabled);

        // then
        assertDoesNotThrow(code);
    }
}