package com.sait.peelin.dto.v1.auth;

import lombok.Getter;
import lombok.Setter;

public class AuthResponse {

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String role;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
}
