/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import ro.idp.upb.authservice.config.JwtService;
import ro.idp.upb.authservice.data.dto.request.AuthenticationRequest;
import ro.idp.upb.authservice.data.dto.request.RegisterRequest;
import ro.idp.upb.authservice.data.dto.response.AuthenticationResponse;
import ro.idp.upb.authservice.data.entity.User;
import ro.idp.upb.authservice.exception.InvalidTokenException;
import ro.idp.upb.authservice.exception.NotRefreshTokenException;
import ro.idp.upb.authservice.exception.MissingTokenException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
	private final UserService userService;
	private final TokenService tokenService;
	private final JwtService jwtService;

	public AuthenticationResponse register(RegisterRequest request) {
		log.info(
				"Register request! [Firstname: {}], [Lastname: {}], [Email: {}]!",
				request.getFirstName(),
				request.getLastName(),
				request.getEmail());
		var registeredUser = userService.registerUser(request);
		AuthenticationResponse tokensResponse = generateAndSaveTokens(registeredUser);
		log.info(
				"Register request for [Firstname: {}], [Lastname: {}], [Email: {}]! done!",
				request.getFirstName(),
				request.getLastName(),
				request.getEmail());
		return tokensResponse;
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		log.info("Authenticate (login) request for user {}!", request.getEmail());
		User user = userService.isAuthRequestValid(request);
		AuthenticationResponse tokensResponse = generateAndSaveTokens(user);
		log.info("Authentication (login) request for {} done!", request.getEmail());
		return tokensResponse;
	}

	private AuthenticationResponse generateAndSaveTokens(User user) {
		log.info("Generate and save tokens for user {}!", user.getId());
		var jwtToken = jwtService.generateToken(user);
		var refreshToken = jwtService.generateRefreshToken(user);
		log.info("Tokens generation for user {} done, saving...", user.getId());
		tokenService.saveUserTokens(user, jwtToken, refreshToken);
		log.info("Tokens saved successfully to IO SERVICE for user {}!", user.getId());
		AuthenticationResponse tokensResponse =
				AuthenticationResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
		return tokensResponse;
	}

	public AuthenticationResponse refreshToken(HttpServletRequest request) {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.error("Token refresh request but missing refresh token in auth header!");
			throw new MissingTokenException();
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if (userEmail != null) {
			User user = userService.findByEmail(userEmail);
			log.info("Token refresh request for user {}!", user.getId());
			if (jwtService.isTokenValid(refreshToken, user)) {
				if (isRefreshToken(refreshToken)) {
					log.info("Refresh token is valid (not expired/revoked) and is actually refresh token!");
					AuthenticationResponse tokensResponse = generateAndSaveTokens(user);
					tokenService.revokeToken(refreshToken);
					log.info("Refresh token request is done for user {}!", user.getId());
					return tokensResponse;
				}
				log.error("Token refresh provided token {} is not valid", refreshToken.substring(0, 15));
				throw new NotRefreshTokenException(refreshToken);
			}
			log.error("Token refresh for user {} went wrong!", user.getId());
		}
		log.error("Missing user email from refresh token!");
		throw new InvalidTokenException();
	}

	public boolean isRefreshToken(String refreshToken) {
		return tokenService.isRefreshToken(refreshToken);
	}
}
