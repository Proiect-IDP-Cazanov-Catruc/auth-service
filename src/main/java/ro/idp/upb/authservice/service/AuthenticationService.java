package ro.idp.upb.authservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
public class AuthenticationService {
    private final UserService userService;
    private final TokenService tokenService;
    private final JwtService jwtService;

    public ResponseEntity<?> register(RegisterRequest request) {
        var savedUser = userService.registerUser(request);
        if (savedUser.isPresent()) {
            User user = savedUser.get();
            ResponseEntity<?> tokensResponse = generateAndSaveTokens(user);
            if (tokensResponse != null) return tokensResponse;
        }
        return ResponseEntity.internalServerError().build();
    }

    public ResponseEntity<?> authenticate(AuthenticationRequest request) {
        var user = userService.findByEmail(request.getEmail())
                .orElseThrow();
        ResponseEntity<?> tokensResponse = generateAndSaveTokens(user);
        if (tokensResponse != null) return tokensResponse;
        return ResponseEntity.internalServerError().build();
    }

    private ResponseEntity<?> generateAndSaveTokens(User user) {
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        ResponseEntity<?> saveResponse = tokenService.saveUserTokens(user, jwtToken, refreshToken);
        if (saveResponse.getStatusCode().is2xxSuccessful()) {
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = userService.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                if (isRefreshToken(refreshToken)) {
                    ResponseEntity<?> tokensResponse = generateAndSaveTokens(user);
                    tokenService.revokeToken(refreshToken);
                    if (tokensResponse != null) return tokensResponse;
                }
            }
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.notFound().build();
    }

    public boolean isRefreshToken(String refreshToken) {
        return tokenService.isRefreshToken(refreshToken);
    }
}
