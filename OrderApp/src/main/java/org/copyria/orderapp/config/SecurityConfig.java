package org.copyria.orderapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> Optional.of(jwt)
                .map(this::extractRealmAccess)
                .map(this::extractRoles)
                .stream()
                .flatMap(List::stream)
                .map("ROLE_%s"::formatted)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(ArrayList::new)));

        return jwtAuthenticationConverter;
    }

    private Map<String, Object> extractRealmAccess(Jwt jwt) {
        if (jwt.hasClaim("realm_access")) {
            return jwt.getClaimAsMap("realm_access");
        } else {
            return Collections.emptyMap();
        }
    }

    private List<String> extractRoles(Map<String, Object> realmAccess) {
        return (List<String>) realmAccess.getOrDefault("roles", Collections.emptyList());
    }
}
