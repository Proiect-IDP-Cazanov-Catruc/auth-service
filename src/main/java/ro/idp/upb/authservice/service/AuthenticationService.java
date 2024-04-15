package ro.idp.upb.authservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ro.idp.upb.authservice.config.JwtService;
import ro.idp.upb.authservice.data.dto.request.AuthenticationRequest;
import ro.idp.upb.authservice.data.dto.request.RegisterRequest;
import ro.idp.upb.authservice.data.dto.response.AuthenticationResponse;
import ro.idp.upb.authservice.data.entity.User;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserService userService;
    private final TokenService tokenService;
    private final JwtService jwtService;

    public ResponseEntity<?> register(RegisterRequest request) {
        log.info("Register request! email {} firstName{} lastName {}!", request.getEmail(),
                request.getFirstName(), request.getLastName());
        var savedUser = userService.registerUser(request);
        if (savedUser.isPresent()) {
            User user = savedUser.get();
            ResponseEntity<?> tokensResponse = generateAndSaveTokens(user);
            if (tokensResponse != null) {
                log.info("Register request for {} done!", request.getEmail());
                return tokensResponse;
            }
        }
        log.error("Register request for {} went wrong!", request.getEmail());
        return ResponseEntity.internalServerError().build();
    }

    public ResponseEntity<?> authenticate(AuthenticationRequest request) {
        log.info("Authenticate (login) request for user {}!", request.getEmail());
        var user = userService.findByEmail(request.getEmail())
                .orElseThrow();
        ResponseEntity<?> tokensResponse = generateAndSaveTokens(user);
        if (tokensResponse != null) {
            log.info("Authentication (login) request for {} done!", request.getEmail());
            return tokensResponse;
        }
        log.error("Authentication (login) request for {} went wrong!", request.getEmail());
        return ResponseEntity.internalServerError().build();
    }

    private ResponseEntity<?> generateAndSaveTokens(User user) {
        log.info("Generate and save tokens for user {}!",
                user.getId());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        log.info("Tokens generation for user {} done, saving...", user.getId());
        ResponseEntity<?> saveResponse = tokenService.saveUserTokens(user, jwtToken, refreshToken);
        if (saveResponse.getStatusCode().is2xxSuccessful()) {
            log.info("Tokens saved successfully to IO SERVICE for user {}!", user.getId());
            AuthenticationResponse tokensResponse = AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
            return ResponseEntity.ok(tokensResponse);
        }
        return null;
    }

    public ResponseEntity<?> refreshToken(
            HttpServletRequest request
    ) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            log.error("Token refresh request but missing refresh token in auth header!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = userService.findByEmail(userEmail)
                    .orElseThrow();
            log.info("Token refresh request for user {}!", user.getId());
            if (jwtService.isTokenValid(refreshToken, user)) {
                if (isRefreshToken(refreshToken)) {
                    log.info("Refresh token is valid (not expired/revoked) and is actually refresh token!");
                    ResponseEntity<?> tokensResponse = generateAndSaveTokens(user);
                    tokenService.revokeToken(refreshToken);
                    if (tokensResponse != null) {
                        log.info("Refresh token request is done for user {}!", user.getId());
                        return tokensResponse;
                    }
                }
            }
            log.error("Token refresh for user {} went wrong!", user.getId());
            return ResponseEntity.internalServerError().build();
        }
        log.error("Missing user email from refresh token!");
        return ResponseEntity.notFound().build();
    }

    public boolean isRefreshToken(String refreshToken) {
        return tokenService.isRefreshToken(refreshToken);
    }
}
