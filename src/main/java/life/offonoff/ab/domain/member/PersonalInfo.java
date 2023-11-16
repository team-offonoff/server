package life.offonoff.ab.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class PersonalInfo {

    private String name;

    @Column(length = 40)
    private String nickname;
    private LocalDate birthDate;
    private Gender gender;
    private String job;

    // w name
    public PersonalInfo(String nickname, LocalDate birthDate, Gender gender, String job) {
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.gender = gender;
        this.job = job;
    }

    // wo name
    public PersonalInfo(String name, String nickname, LocalDate birthDate, Gender gender, String job) {
        this.name = name;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.gender = gender;
        this.job = job;
    }
}