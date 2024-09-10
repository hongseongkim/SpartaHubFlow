package com.sparta.hotsix.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class HeaderModifierFilter implements GlobalFilter  {
    @Value("${service.jwt.secret-key}")
    private String secretKey;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 특정 요청 경로 및 메서드에 대해서만 헤더 추가
        if (shouldAddHeader(exchange)) {

            String token = extractToken(exchange);
            if (token != null) {
                Claims claims = extractUserEmailFromToken(token);

                assert claims != null;

                String id = claims.getId();
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("User-Id", id)
                        .header("User-Email" , email)
                        .header("User-Role" , role)
                        .build();

                System.out.println("modifiedRequest = " + modifiedRequest.getHeaders());

                exchange = exchange.mutate()
                        .request(modifiedRequest)
                        .build();


            } else {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }




    private boolean shouldAddHeader(ServerWebExchange exchange) {
        // 조건에 따라 헤더를 추가할 요청 경로와 메서드를 필터링
        HttpMethod method = exchange.getRequest().getMethod();
        // GET 메서드인 경우에만 헤더를 추가
        return method.matches("GET");

    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }


    private Claims extractUserEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey)))
                    .build().parseSignedClaims(token).getPayload();
            return claims;
        } catch (Exception e) {
            return null;
        }
    }


}
