// Contributor(s): Robbie
// Main: Robbie - Logout and revocation checks for JWT identifiers in Redis or memory.

package com.sait.peelin.service;

/** Abstraction for denylisting JWTs until expiry after logout or security events. */
public interface TokenDenylistService {
    void deny(String token);
    boolean isDenied(String token);
}
