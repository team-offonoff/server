package life.offonoff.ab.application.service.authenticate.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class KakaoTokenResponse {

    @JsonProperty(value = "token_type")
    private String tokenType;
    @JsonProperty(value = "access_token")
    private String accessToken;
    @JsonProperty(value = "refresh_token")
    private String refreshToken;
    @JsonProperty(value = "refresh_token_expires_in")
    private Long refreshTokenExpiresIn;
    @JsonProperty(value = "id_token")
    private String idToken;
    @JsonProperty(value = "expires_in")
    private Long expiresIn;
    private String scope;
}
