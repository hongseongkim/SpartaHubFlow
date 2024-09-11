package com.sparta.hotsix.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();

        if (token != null && validateToken(token)) {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey)))
                    .build().parseSignedClaims(token).getPayload();

            String userEmail = claims.getSubject();
            String userRole = claims.get("role", String.class);
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" +userRole));

            UserDetails userDetails = User.withUsername(userEmail)
                    .roles(userRole)
                    .password("") // 비밀번호는 필요 없으므로 빈 문자열
                    .authorities(authorities)
                    .build();

            System.out.println("userDetails = " + userDetails);
            return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, token, authorities));
        }

        System.out.println("token = " + token);

        return Mono.empty();
    }


    private boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey)))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
