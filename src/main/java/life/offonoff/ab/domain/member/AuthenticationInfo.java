package life.offonoff.ab.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class AuthenticationInfo {
    @Column(unique = true)
    private String email;

    private String password;
    @Enumerated(EnumType.STRING)
    private Provider provider;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    public AuthenticationInfo(String email, String password, Provider provider) {
        this.email = email;
        this.password = password;
        this.provider = provider;
    }
}
