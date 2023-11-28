package life.offonoff.ab.application.service.request.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import life.offonoff.ab.domain.member.Gender;
import life.offonoff.ab.domain.member.PersonalInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRegisterRequest {

    private Long memberId;
    private String nickname;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private Gender gender;
    private String job;

    public PersonalInfo toPersonalInfo() {
        return new PersonalInfo(nickname, birth, gender, job);
    }
}
