// Contributor(s): Robbie
// Main: Robbie - JWT login registration OAuth2 callback and password reset for Spring Security.

package com.sait.peelin.controller.v1;

import com.sait.peelin.dto.v1.auth.AuthResponse;
import com.sait.peelin.dto.v1.auth.ForgotPasswordRequest;
import com.sait.peelin.dto.v1.auth.LoginRequest;
import com.sait.peelin.dto.v1.auth.RegisterAvailabilityResponse;
import com.sait.peelin.dto.v1.auth.RegisterRequest;
import com.sait.peelin.dto.v1.auth.ResetPasswordRequest;
import com.sait.peelin.model.User;
import com.sait.peelin.service.AuthService;
import com.sait.peelin.service.CurrentUserService;
import com.sait.peelin.service.JwtService;
import com.sait.peelin.service.PasswordResetService;
import com.sait.peelin.service.TokenDenylistService;
import com.sait.peelin.service.WelcomeEmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

/**
 * REST endpoints under {@code /api/v1/auth}. Cookie and JSON responses follow the contract used by the SPA and mobile apps.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Register login and JWT issuance for SPA and mobile API access")
public class AuthController {

    private final AuthService authService;
    private final TokenDenylistService tokenDenylistService;
    private final PasswordResetService passwordResetService;
    private final JwtService jwtService;
    private final WelcomeEmailService welcomeEmailService;
    private final CurrentUserService currentUserService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.jwt.expiration:864000000}")
    private long jwtExpiration;

    // Secure-by-default. Dev profile overrides to false via application-dev.yaml so that
    // localhost (http://) logins still set the cookie. Never set this to false in prod.
    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    private void setTokenCookie(HttpServletResponse response, String token, boolean rememberMe) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(rememberMe ? Duration.ofMillis(jwtExpiration) : Duration.ofSeconds(-1))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Operation(summary = "Session snapshot", description = "Returns whether the caller is authenticated plus user id username and role when a token is present.")
    @ApiResponse(responseCode = "200", description = "Snapshot returned")
    @GetMapping("/whoami")
    public ResponseEntity<java.util.Map<String, Object>> whoami() {
        User u = currentUserService.currentUserOrNull();
        if (u == null) {
            return ResponseEntity.ok(java.util.Map.of("authenticated", false));
        }
        return ResponseEntity.ok(java.util.Map.of(
                "authenticated", true,
                "userId", u.getUserId().toString(),
                "username", u.getUsername(),
                "role", u.getUserRole().name()
        ));
    }

    @Operation(summary = "Log in", description = "Authenticates credentials and returns a JWT plus embedded user summary. Sets the HTTP-only session cookie when remember-me is enabled.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login succeeded"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest);

        setTokenCookie(response, authResponse.getToken(), loginRequest.isRememberMe());

        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Register availability", description = "Check username and email before completing a multi-step registration (case-insensitive).")
    @GetMapping("/register/availability")
    public ResponseEntity<RegisterAvailabilityResponse> registerAvailability(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone) {
        return ResponseEntity.ok(authService.getRegisterAvailability(username, email, phone));
    }

    @Operation(summary = "Register", description = "Create a new customer account. Returns a JWT token immediately upon success.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created, token returned"),
            @ApiResponse(responseCode = "400", description = "Validation error or email already in use", content = @Content)
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.register(registerRequest);

        setTokenCookie(response, authResponse.getToken(), true);

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @Operation(summary = "Log out", description = "Invalidates the current JWT token. The token is added to a denylist and will be rejected on subsequent requests.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenDenylistService.deny(authHeader.substring(7));
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("token".equals(c.getName())
                        && c.getValue() != null && !c.getValue().isBlank()) {
                    tokenDenylistService.deny(c.getValue());
                }
            }
        }

        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        ResponseCookie sessionCookie = ResponseCookie.from("JSESSIONID", "")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie.toString());

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "OAuth2 callback (not yet implemented)", description = "Placeholder for future Google/Microsoft OAuth2 login flow.")
    @ApiResponse(responseCode = "501", description = "Not implemented", content = @Content)
    @PostMapping("/oauth2/callback")
    public ResponseEntity<String> oauth2Callback() {
        // TODO: implement OAuth2 login (Google/Microsoft)
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("OAuth2 login is scaffolded but not yet implemented");
    }

    @Operation(summary = "Request password reset", description = "Queues a reset email when the address matches an account without leaking whether it exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request accepted"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.getEmail());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Complete password reset", description = "Applies a new password when the emailed reset token is still valid.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token", content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "OAuth2 success redirect", description = "Browser-only redirect that sets the auth cookie then sends the user to the SPA auth callback.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "Redirect to frontend"),
            @ApiResponse(responseCode = "401", description = "OAuth session missing token", content = @Content)
    })
    @GetMapping("/oauth2/success")
    public void oauth2Success(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        String token = (String) request.getSession().getAttribute("oauth2_pending_token");
        if (token == null) {
            response.sendRedirect(frontendUrl + "/login?error=oauth_failed");
            return;
        }
        request.getSession().removeAttribute("oauth2_pending_token");
        setTokenCookie(response, token, true);
        AuthResponse auth = authService.getUserInfoFromToken(token);
        String q = "username=" + URLEncoder.encode(auth.getUsername(), StandardCharsets.UTF_8)
                + "&role=" + URLEncoder.encode(auth.getRole(), StandardCharsets.UTF_8)
                + "&userId=" + URLEncoder.encode(String.valueOf(auth.getUserId()), StandardCharsets.UTF_8);
        response.sendRedirect(frontendUrl + "/auth/callback?" + q);
    }

    @Operation(summary = "Validate reset token", description = "Returns 200 when the emailed token is still valid before showing the reset form.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token valid"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired token", content = @Content)
    })
    @GetMapping("/reset-password/validate")
    public ResponseEntity<Void> validateResetToken(@RequestParam String token) {
        passwordResetService.validateToken(token);
        return ResponseEntity.ok().build();
    }
}
