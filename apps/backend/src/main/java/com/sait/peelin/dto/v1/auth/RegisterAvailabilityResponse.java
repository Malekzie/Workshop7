package com.sait.peelin.dto.v1.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pre-check before multi-step registration: username must be unique; sign-in email must not belong to another
 * customer or admin account. Employee accounts may share that email so customer registration can match work email
 * for employee–customer linking.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAvailabilityResponse {
    private boolean usernameAvailable;
    private boolean emailAvailable;
    /**
     * True when this email can be used for a new customer account and matches exactly one unlinked employee work email,
     * so the client should collect the employee account password before calling {@code register}.
     */
    private boolean employeeLinkOffered;
    /** True when this sign-in email matches guest checkout rows that can be linked after registration. */
    private boolean guestEmailLinkOffered;
    /** True when this phone matches guest checkout rows that can be linked after registration. */
    private boolean guestPhoneLinkOffered;
}
