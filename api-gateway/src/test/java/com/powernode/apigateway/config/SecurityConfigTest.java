package com.powernode.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    @Test
    void mapsAdminClaimToRoleAdminAuthority() {
        SecurityConfig securityConfig = new SecurityConfig();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .subject("1")
                .claim("role", "ADMIN")
                .build();

        var authentication = securityConfig.jwtAuthenticationConverter()
                .convert(jwt)
                .block();

        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
    }
}
