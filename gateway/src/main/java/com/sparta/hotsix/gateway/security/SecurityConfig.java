package com.sparta.hotsix.gateway.security;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.server.ServerWebExchange;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public GlobalFilter headerModifierFilter() {
        return new HeaderModifierFilter();
    }

    @Bean
    public JwtAuthenticationManager jwtAuthenticationManager() {
        return new JwtAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }

    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter());

        authenticationWebFilter.setAuthenticationFailureHandler((webFilterExchange, exception) -> {
            // 인증 실패 시 403 Forbidden 응답을 반환
            ServerWebExchange exchange = webFilterExchange.getExchange();
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        });

        return authenticationWebFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("specific_route", r -> r
                        .path("/api/v1/specific-endpoint/**")
                        .filters(f -> f
                                .addRequestHeader("X-User-Email", "extracted-email"))  // 이 부분에서 헤더 추가
                        .uri("http://your-service-url"))
                .build();
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/user/signUp").permitAll()
                        .pathMatchers("/api/v1/user/signIn").permitAll()
                        .pathMatchers("/swagger-ui/**").permitAll()
                        .pathMatchers("/api/v1/user/docs").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/user/admin","/api/v1/user/search").hasRole("MASTER")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/user/admin").hasRole("MASTER")
                        .pathMatchers("/api/v1/user/**").authenticated()

                        .pathMatchers("/api/v1/user/**").hasRole("MASTER")


                        .pathMatchers(HttpMethod.GET, "/api/v1/hubs/**").permitAll()
                        .pathMatchers("/api/v1/hubs/**").hasRole("MASTER")


                        .pathMatchers("/api/v1/products/**").hasRole("MASTER")
                        .pathMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()


                        .anyExchange().authenticated())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)


                .authenticationManager(jwtAuthenticationManager())


                .addFilterBefore(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();

    }
}