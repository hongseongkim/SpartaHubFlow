package com.sparta.hotsix.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String token = extractToken(exchange);

        if (token != null && validateToken(token)) {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey)))
                    .build().parseSignedClaims(token).getPayload();

            String userRole =  claims.get("role", String.class); // 접두사 추가
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(userRole));

            UserDetails userDetails = User.withUsername(claims.getSubject())
                    .roles(userRole)
                    .password("") // 비밀번호는 필요 없으므로 빈 문자열
                    .authorities(authorities)
                    .build();

            System.out.println("Extracted role: " + userRole);
            System.out.println("Authorities: " + authorities);

            return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, token, authorities));
        }

        return Mono.empty();
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey)))
                    .build()
                    . parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
