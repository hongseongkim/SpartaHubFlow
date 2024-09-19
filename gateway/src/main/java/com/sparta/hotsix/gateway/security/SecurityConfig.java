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

    static final String v1 = "/api/v1/";


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

//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("specific_route", r -> r
//                        .path("/api/v1/specific-endpoint/**")
//                        .filters(f -> f
//                                .addRequestHeader("X-User-Email", "extracted-email"))  //
//                        .uri("http://your-service-url"))
//                .build();
//    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        //user
                        .pathMatchers(v1 + "user/signUp").permitAll()
                        .pathMatchers(v1 + "user/signIn").permitAll()
                        .pathMatchers("/swagger-ui/**").permitAll()
                        .pathMatchers(v1 + "user/docs").permitAll()
                        .pathMatchers(HttpMethod.GET, v1 + "user/admin").hasRole("MASTER")
                        .pathMatchers(HttpMethod.GET, v1 + "user/search").hasRole("MASTER")
                        .pathMatchers(HttpMethod.DELETE, v1 + "user/admin").hasRole("MASTER")


                        //허브
                        .pathMatchers(HttpMethod.GET, "/api/v1/hubs/").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/hubs/**").permitAll()
                        .pathMatchers("/api/v1/hubs").hasRole("MASTER")
                        .pathMatchers(HttpMethod.POST,v1 + "hubs").hasAnyRole("MASTER", "HUB_MANAGER")
                        .pathMatchers(HttpMethod.POST,v1 + "hubs/**").hasAnyRole("MASTER", "HUB_MANAGER")
                        .pathMatchers(HttpMethod.PATCH,v1 + "orders/**").hasAnyRole("MASTER", "HUB_MANAGER")
                        .pathMatchers(HttpMethod.DELETE,v1 + "orders/**").hasAnyRole("MASTER", "HUB_MANAGER")


                        //상품
                        .pathMatchers(v1 + "products/**").hasRole("MASTER")
                        .pathMatchers(HttpMethod.POST,v1 + "products").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")
                        .pathMatchers(HttpMethod.PATCH,v1 + "products").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")
                        .pathMatchers(HttpMethod.DELETE,v1 + "products").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")

                        //업체
                        .pathMatchers(HttpMethod.POST,v1 + "companys").hasAnyRole("MASTER", "HUB_MANAGER")
                        .pathMatchers(v1 + "companys/**").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")

                        //주문
                        .pathMatchers(HttpMethod.POST,v1 + "orders").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")
                        .pathMatchers(HttpMethod.PATCH,v1 + "orders/**").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")
                        .pathMatchers(HttpMethod.DELETE,v1 + "orders/**").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")

                        //배달관련
                        .pathMatchers(HttpMethod.POST,v1 + "deliveries").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")
                        .pathMatchers(HttpMethod.POST,v1 + "deliveries/**").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")
                        .pathMatchers(HttpMethod.PATCH,v1 + "deliveries/**").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")
                        .pathMatchers(HttpMethod.DELETE,v1 + "deliveries/**").hasAnyRole("MASTER", "HUB_MANAGER","COMPANY_MANAGER")
                        .anyExchange().authenticated())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)


                .authenticationManager(jwtAuthenticationManager())


                .addFilterBefore(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();

    }
}