package com.sait.peelin.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GooglePrivateIpOAuth2AuthorizationRequestResolverTest {

    @Test
    void hostLooksLikePrivateLan_trueFor10x() {
        assertTrue(GooglePrivateIpOAuth2AuthorizationRequestResolver.hostLooksLikePrivateLan(
                "http://10.0.0.57:8080/login/oauth2/code/google"));
    }

    @Test
    void hostLooksLikePrivateLan_falseForLocalhost() {
        assertFalse(GooglePrivateIpOAuth2AuthorizationRequestResolver.hostLooksLikePrivateLan(
                "http://127.0.0.1:8080/cb"));
        assertFalse(GooglePrivateIpOAuth2AuthorizationRequestResolver.hostLooksLikePrivateLan(
                "http://localhost:8080/cb"));
    }

    @Test
    void hostLooksLikePrivateLan_falseForNipIoStyleHost() {
        assertFalse(GooglePrivateIpOAuth2AuthorizationRequestResolver.hostLooksLikePrivateLan(
                "http://10-0-0-57.nip.io:8080/cb"));
    }
}
