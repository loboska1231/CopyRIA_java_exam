package org.copyria.orderapp.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    public Set<String> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        Map<String, Object> realm = jwt.getClaimAsMap("realm_access");

        if (realm == null) {
            return Set.of();
        }

        List<String> roles = (List<String>) realm.getOrDefault("roles", List.<String>of());

        return new HashSet<>(roles);
    }
    public Set<String> getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Map<String, Object> realm = jwt.getClaimAsMap("email");
        if (realm == null) {
            return null;
        }

        List<String> email = (List<String>) realm.getOrDefault("email", List.<String>of());

        return new HashSet<>(email);
    }
}
