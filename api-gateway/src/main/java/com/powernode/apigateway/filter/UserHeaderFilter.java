package com.powernode.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserHeaderFilter implements GlobalFilter, Ordered {


    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {

                    if (!(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
                        return chain.filter(exchange);
                    }
                    var jwt = jwtAuthenticationToken.getToken();

                    String userId = jwt.getSubject();
                    String username = jwt.getClaimAsString("username");
                    String email = jwt.getClaimAsString("email");
                    String status = jwt.getClaimAsString("status");
                    String role = jwt.getClaimAsString("role");


                    ServerWebExchange mutableExchange = exchange.mutate()
                            .request(request -> request.headers(headers -> {
                                headers.set("X-User-Id", userId);
                                headers.set("X-User-Username", username);
                                headers.set("X-User-Email", email);
                                headers.set("X-User-Status", status);
                                headers.set("X-User-Role", role);
                            })).build();
                    return chain.filter(mutableExchange);
                }).switchIfEmpty(chain.filter(exchange));

    }

    @Override
    public int getOrder() {
        return 0;
    }

}
