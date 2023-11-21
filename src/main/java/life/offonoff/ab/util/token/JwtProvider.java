package life.offonoff.ab.util.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import life.offonoff.ab.exception.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.*;

@Service
public class JwtProvider {

    private static final SignatureAlgorithm signAlg = SignatureAlgorithm.HS256;

    private final String secretKey;
    private final Long expiresIn;
    private final JwtParser parser;


    public JwtProvider(
            @Value("${ab.auth.token.jwt.secret-key}") String secretKey,
            @Value("${ab.auth.token.jwt.expires-in}") Long expiresIn
    ) {
        this.secretKey = secretKey;
        this.expiresIn = expiresIn;
        this.parser = Jwts.parserBuilder()
                          .setSigningKey(key())
                          .build();
    }

    //== Generate ==//
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

    //== Parse ==//
    public Long parseMemberId(String accessToken) {
        Claims claims = parser.parseClaimsJws(accessToken)
                             .getBody();
        verity(claims);

        return claims.get("member-id", Long.class);
    }

    private void verity(Claims claims) {
        boolean expired = claims.getExpiration()
                                .before(new Date());

        if (expired) {
            throw new ExpiredJwtException();
        }
    }

    private Key key() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
