package com.sparta.hotsix.user.jwt;

import com.sparta.hotsix.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
@Component
public class JwtUtil {
    private final String secretKey;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${service.jwt.access-expiration}")
    private long expirationMs;

    public long getExpirationMs() {
        return expirationMs;
    }


    public JwtUtil(@Value("${service.jwt.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .id(user.getId().toString())
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .issuer(appName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims =             Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey)))
                    .build()
                    . parseSignedClaims(token);
            return !claims.getPayload().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

}