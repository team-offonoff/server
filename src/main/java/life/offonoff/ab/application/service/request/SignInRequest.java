package life.offonoff.ab.application.service.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInRequest {

    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
