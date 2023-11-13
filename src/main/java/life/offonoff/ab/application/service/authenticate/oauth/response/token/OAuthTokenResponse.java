package life.offonoff.ab.application.service.authenticate.oauth.response.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class OAuthTokenResponse {

    // 현재는 idToken만 필요
    @JsonProperty(value = "id_token")
    private String idToken;
}
