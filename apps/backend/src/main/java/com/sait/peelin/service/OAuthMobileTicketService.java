package com.sait.peelin.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Short-lived, one-time tickets so a native app can obtain a JWT after the server-side
 * OAuth2 login flow (session + {@link com.sait.peelin.security.OAuth2SuccessHandler}).
 */
@Service
public class OAuthMobileTicketService {

    private static final long TTL_MS = 120_000;

    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();

    public String issue(String jwt) {
        String id = UUID.randomUUID().toString();
        tickets.put(id, new Ticket(jwt, System.currentTimeMillis() + TTL_MS));
        return id;
    }

    /**
     * @return the JWT if the ticket was valid and unused; empty if missing or expired
     */
    public Optional<String> claim(String ticketId) {
        if (ticketId == null || ticketId.isBlank()) {
            return Optional.empty();
        }
        Ticket t = tickets.remove(ticketId.trim());
        if (t == null || System.currentTimeMillis() > t.expiresAtMs) {
            return Optional.empty();
        }
        return Optional.of(t.jwt);
    }

    private record Ticket(String jwt, long expiresAtMs) {}
}
