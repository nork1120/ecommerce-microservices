package com.powernode.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Slf4j
@Configuration
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                //Gateway 是 REST API，不使用表單登入
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                //不使用 HTTP Basic
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                // REST API / JWT 架構通常關閉CSRF
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                //設定那些API 可以不用登入
                .authorizeExchange(exchange -> exchange

                        //登入 註冊不用JWT
                        .pathMatchers(
                                "/api/users/register",
                                "/api/users/login"
                        ).permitAll()

                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/openapi/**"
                        ).permitAll()

                        //可以通過的商品API
                        .pathMatchers(
                                "/api/product/getAllCategories",
                                "/api/product/getAll",
                                "/api/product/*"
                        ).permitAll()

                        .pathMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Actuator 先允許
                        .pathMatchers("/actuator/**").permitAll()

                        //其他API 都需要登入
                        .anyExchange().authenticated()
                )
                //開啟 OAuth2 Resource Server JWT 驗證
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint((exchange, exception) -> {
                            // JWT 缺少、過期、格式錯誤等等
                            ServerHttpResponse response = exchange.getResponse();

                            response.setStatusCode(HttpStatus.UNAUTHORIZED);
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                            String body = """
                                    {
                                      "code": 401,
                                      "message": "Token 無效或已過期"
                                    }
                                    """;

                            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

                            return response.writeWith(Flux.just(buffer));
                        })
                )
                .exceptionHandling(exception -> exception.accessDeniedHandler((exchange, denied) -> {

                    ServerHttpResponse response = exchange.getResponse();

                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    response.getHeaders()
                            .setContentType(MediaType.APPLICATION_JSON);

                    String body = """
                            {
                                "code": 403,
                                "message": "您沒有權限存取此資源"
                            }
                            """;

                    DataBuffer buffer = response.bufferFactory()
                            .wrap(body.getBytes(StandardCharsets.UTF_8));

                    return response.writeWith(Flux.just(buffer));

                }))
                .build();


    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {

        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            String role = jwt.getClaimAsString("role");
            if (role == null || role.isBlank()) {
                return Flux.empty();
            }

            String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
            String authority = normalizedRole.startsWith("ROLE_")
                    ? normalizedRole
                    : "ROLE_" + normalizedRole;

            log.debug("JWT role mapped to authority={}", authority);
            return Flux.just(new SimpleGrantedAuthority(authority));


        });

        return converter;
    }

}
