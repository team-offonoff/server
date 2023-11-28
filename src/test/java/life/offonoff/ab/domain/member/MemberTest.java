package life.offonoff.ab.domain.member;

import life.offonoff.ab.exception.IllegalJoinStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("AuthInfo 중복 등록시 예외")
    void register_authInfo_exception() {
        // given
        String email = "email";
        String password = "password";
        Provider provider = Provider.NONE;
        AuthenticationInfo authInfo = new AuthenticationInfo(email, password, provider);

        // when
        Member member = new Member(email, password, provider);

        // then
        assertThatThrownBy(() -> member.registerAuthInfo(authInfo))
                .isInstanceOf(IllegalJoinStatusException.class);
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
    @DisplayName("PersonalInfo 중복 등록시 예외")
    void register_personalInfo_exception() {
        // given
        Member member = new Member("email", "password", Provider.NONE);
        PersonalInfo personalInfo = new PersonalInfo("nickname", LocalDate.now(), Gender.ETC, "job");

        // when
        member.registerPersonalInfo(personalInfo);

        // then
        assertThatThrownBy(() -> member.registerPersonalInfo(personalInfo))
                .isInstanceOf(IllegalJoinStatusException.class);
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
    @DisplayName("TermsEnabled 중복 등록시 예외")
    void register_termsEnabled_exception() {
        // given
        Member member = new Member("nickname", LocalDate.now(), Gender.ETC, "job", NotificationEnabled.allEnabled());
        TermsEnabled termsEnabled = new TermsEnabled(true);

        // when
        member.agreeTerms(termsEnabled);

        // then
        assertThatThrownBy(() -> member.agreeTerms(termsEnabled))
                .isInstanceOf(IllegalJoinStatusException.class);
    }

}