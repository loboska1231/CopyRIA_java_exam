package org.copyria.orderapp.config;

import lombok.RequiredArgsConstructor;
import org.copyria.orderapp.client.rest.ApiClient;
import org.copyria.orderapp.client.rest.api.CarApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@RequiredArgsConstructor
public class CarApiConfig {
    private final OAuth2AuthorizedClientManager auth2AuthorizedClientManager;
    @Bean("userAuthApiClient")
    public ApiClient userAuthApiClient(
            @Value("${app.apis.car-service.base-url}") String baseUrl
    ) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(() -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return jwt.getTokenValue();
        });
        return apiClient;
    }
    @Bean("serviceAuthApiClient")
    public ApiClient searviceAuthApiClient(
            @Value("${app.apis.car-service.base-url}") String baseUrl
    ) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(() -> {
            OAuth2AuthorizeRequest oauth2Request = OAuth2AuthorizeRequest
                    .withClientRegistrationId("order-service-client")
                    .principal("order-service").build();
            OAuth2AuthorizedClient client = auth2AuthorizedClientManager.authorize(oauth2Request);

            return client.getAccessToken().getTokenValue();
        });
        return apiClient;
    }
    @Bean("userAuthCarApi")
    public CarApi userAuthCarApi(@Qualifier("userAuthApiClient") ApiClient apiClient) {
        return new CarApi(apiClient);
    }

    @Bean("serviceAuthCarApi")
    public CarApi serviceAuthCarApi(@Qualifier("serviceAuthApiClient") ApiClient apiClient) {
        return new CarApi(apiClient);
    }
}
