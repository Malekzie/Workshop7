package com.sait.peelin.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Google returns {@code invalid_request} with
 * "device_id and device_name are required for private IP" when {@code redirect_uri} uses a bare
 * RFC&nbsp;1918 host (e.g. {@code 10.0.0.57}). Localhost / 127.0.0.1 are exempt. Adding these
 * parameters satisfies Google's check for typical LAN dev (physical device + machine IP).
 */
@Component
public class GooglePrivateIpOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String REGISTRATION_ID_GOOGLE = "google";
    private static final String AUTHORIZATION_BASE_URI = "/oauth2/authorization";

    private final OAuth2AuthorizationRequestResolver delegate;

    public GooglePrivateIpOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                AUTHORIZATION_BASE_URI
        );
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return augment(delegate.resolve(request), request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return augment(delegate.resolve(request, clientRegistrationId), request);
    }

    private OAuth2AuthorizationRequest augment(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request) {
        if (authorizationRequest == null) {
            return null;
        }
        String registrationId = extractRegistrationId(request);
        if (!REGISTRATION_ID_GOOGLE.equals(registrationId)) {
            return authorizationRequest;
        }
        String redirectUri = authorizationRequest.getRedirectUri();
        if (redirectUri == null || !hostLooksLikePrivateLan(redirectUri)) {
            return authorizationRequest;
        }
        Map<String, Object> additional = new HashMap<>(authorizationRequest.getAdditionalParameters());
        additional.putIfAbsent("device_id", "peelin-lan-" + UUID.randomUUID());
        additional.putIfAbsent("device_name", "Peelin-Good-LAN-dev");
        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(additional)
                .build();
    }

    private static String extractRegistrationId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String prefix = AUTHORIZATION_BASE_URI + "/";
        if (!uri.startsWith(prefix)) {
            return null;
        }
        String rest = uri.substring(prefix.length());
        int slash = rest.indexOf('/');
        return slash < 0 ? rest : rest.substring(0, slash);
    }

    /**
     * True for common private IPv4 literals Google treats as "private IP" in this error (not localhost).
     */
    static boolean hostLooksLikePrivateLan(String redirectUri) {
        try {
            URI u = URI.create(redirectUri);
            String host = u.getHost();
            if (host == null || host.isEmpty()) {
                return false;
            }
            if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host)) {
                return false;
            }
            if (host.startsWith("10.")) {
                return true;
            }
            if (host.startsWith("192.168.")) {
                return true;
            }
            if (host.startsWith("172.")) {
                String[] parts = host.split("\\.");
                if (parts.length >= 2) {
                    int second = Integer.parseInt(parts[1]);
                    return second >= 16 && second <= 31;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
