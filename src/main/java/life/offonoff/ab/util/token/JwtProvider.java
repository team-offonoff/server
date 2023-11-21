package life.offonoff.ab.util.jwt.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.*;

public class JwtGenerator {

    private static final SignatureAlgorithm signAlg = SignatureAlgorithm.HS256;

    @Value("${ab.auth.token.jwt.secret-key}")
    private String secretKey;

    @Value("${ab.auth.token.jwt.expires-in}")
    private Long expiresIn;

    public String generateAccessToken(Long memberId) {
        return Jwts.builder()
                .setHeader(header())
                .setClaims(payloads(memberId))
                .setExpiration(exp(currentTimeMillis()))
                .signWith(key(), signAlg)
                .compact();
    }

    private Map<String, Object> header() {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", signAlg.getValue());
        header.put("typ", "JWT");

        return header;
    }

    private Map<String, Object> payloads(Long memberId) {
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("member-id", memberId);

        return payloads;
    }

    private Date exp(long currentTime) {
        Date date = new Date();
        date.setTime(currentTime + expiresIn);

        return date;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
