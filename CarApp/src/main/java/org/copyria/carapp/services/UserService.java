package org.copyria.carapp.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private Map<String, Object> getUserAttributes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        if (jwt.hasClaim("attributes")) {
            return jwt.getClaimAsMap("attributes");
        } else {
            return Collections.emptyMap();
        }
    }

    public Set<String> getUserAssignedShopIds() {
        Map<String, Object> attributes = getUserAttributes();

        if (attributes.containsKey("user_assigned_shop_ids")) {
            List<String> userAssignedShopIds = (List<String>) attributes.get("user_assigned_shop_ids");
            return new HashSet<>(userAssignedShopIds);
        } else {
            return Collections.emptySet();
        }
    }
}
