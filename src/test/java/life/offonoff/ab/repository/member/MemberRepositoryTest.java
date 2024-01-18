package life.offonoff.ab.repository.member;

import life.offonoff.ab.configuration.TestConfig;
import life.offonoff.ab.domain.TestEntityUtil;
import life.offonoff.ab.domain.TestEntityUtil.TestMember;
import life.offonoff.ab.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestConfig.class)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("닉네임 중복 등록시에는 예외")
    void nickname_exists() {
        // given
        String nickname = "nickname";

        Member member = TestMember.builder()
                .email("email")
                .nickname(nickname)
                .build().buildMember();

        memberRepository.save(member);

        // when
        boolean nicknameExists = memberRepository.existsByNickname(nickname);

        // then
        Assertions.assertThat(nicknameExists).isTrue();
    }
}