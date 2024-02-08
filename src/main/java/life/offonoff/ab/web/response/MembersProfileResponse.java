package life.offonoff.ab.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import life.offonoff.ab.domain.member.Gender;
import life.offonoff.ab.domain.member.Member;
import life.offonoff.ab.domain.member.PersonalInfo;

import java.time.LocalDate;

public record MembersProfileResponse (
        String profileImageUrl,
        String nickname,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birth,
        Gender gender,
        String job
){
    public static MembersProfileResponse from(Member member) {
        PersonalInfo personalInfo = member.getPersonalInfo();
        return new MembersProfileResponse(
                member.getProfileImageUrl(),
                personalInfo.getNickname(),
                personalInfo.getBirthDate(),
                personalInfo.getGender(),
                personalInfo.getJob()
        );
    }
}
