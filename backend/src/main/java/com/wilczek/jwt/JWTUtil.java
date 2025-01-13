package com.wilczek.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JWTUtil {

    private static final String SECRET_KEY = "wilczek_1@3$5^7*9)_wilczek_1@3$5^7*9)_wilczek_1@3$5^7*9)_wilczek_1@3$5^7*9)";

    public String issueToken(String subject){
        return issueToken(subject,Map.of());
    }
    public String issueToken(String subject, String ...scopes){
        return issueToken(subject,Map.of("scopes",scopes));
    }

    public String issueToken(
            String subject,
            Map<String,Object> claims) {
        String tokenJWS = Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer("https://amigoscode.com")
                .issuedAt(Date.from(Instant.now()))
                .expiration(
                        Date.from(Instant.now().plus(3600, ChronoUnit.SECONDS))
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return tokenJWS;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
