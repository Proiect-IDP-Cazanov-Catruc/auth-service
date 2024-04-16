/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ro.idp.upb.authservice.data.dto.request.PostTokensDto;
import ro.idp.upb.authservice.data.dto.response.TokenDto;
import ro.idp.upb.authservice.data.entity.Token;
import ro.idp.upb.authservice.data.entity.User;
import ro.idp.upb.authservice.data.enums.TokenType;
import ro.idp.upb.authservice.utils.StaticConstants;
import ro.idp.upb.authservice.utils.UrlBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
	private final StaticConstants staticConstants;

	public Optional<Token> findByToken(String token, TokenType tokenType) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		Map<String, Object> params = new HashMap<>();
		params.put("io-service-url", staticConstants.ioServiceUrl);
		params.put("tokens-endpoint", staticConstants.tokensEndpoint);
		params.put("token-type", tokenType);
		params.put("token", token);

		String url =
				UrlBuilder.replacePlaceholdersInString(
						"${io-service-url}/${tokens-endpoint}/${token-type}/${token}", params);

		log.info("Find by token request to IO SERVICE...");

		String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();

		ResponseEntity<TokenDto> response;
		try {
			response = restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, TokenDto.class);
		} catch (HttpStatusCodeException e) {
			log.error("Find by token request to IO SERVICE is not 2xx successful!");
			return Optional.empty();
		}
		log.info("Fetched token details by token from IO SERVICE");
		TokenDto dtoResponse = response.getBody();
		TokenDto associatedTokenDtoResponse = dtoResponse.getAssociatedToken();
		Token tokenEntity =
				Token.builder()
						.id(dtoResponse.getId())
						.token(dtoResponse.getToken())
						.tokenType(dtoResponse.getTokenType())
						.revoked(dtoResponse.getRevoked())
						.expired(dtoResponse.getExpired())
						.userId(dtoResponse.getUserId())
						.build();
		Token associatedTokenEntity =
				Token.builder()
						.id(associatedTokenDtoResponse.getId())
						.token(associatedTokenDtoResponse.getToken())
						.tokenType(associatedTokenDtoResponse.getTokenType())
						.revoked(associatedTokenDtoResponse.getRevoked())
						.expired(associatedTokenDtoResponse.getExpired())
						.userId(associatedTokenDtoResponse.getUserId())
						.build();

		tokenEntity.setAssociatedToken(associatedTokenEntity);

		return Optional.of(tokenEntity);
	}

	public void handleTokenLogout(String jwt) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		Map<String, Object> params = new HashMap<>();
		params.put("io-service-url", staticConstants.ioServiceUrl);
		params.put("tokens-endpoint", staticConstants.tokensEndpoint);
		params.put("token-logout", staticConstants.tokenLogoutEndpoint);
		params.put("token", jwt);

		String url =
				UrlBuilder.replacePlaceholdersInString(
						"${io-service-url}${tokens-endpoint}/${token-logout}/${token}", params);

		log.info("Token logout request to IO SERVICE!");

		String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();

		restTemplate.exchange(urlTemplate, HttpMethod.POST, entity, Void.class);
	}

	public ResponseEntity<?> saveUserTokens(User user, String jwtToken, String refreshToken) {
		PostTokensDto dto =
				PostTokensDto.builder()
						.accessToken(jwtToken)
						.refreshToken(refreshToken)
						.userId(user.getId())
						.build();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(dto, headers);

		Map<String, Object> params = new HashMap<>();
		params.put("io-service-url", staticConstants.ioServiceUrl);
		params.put("tokens-endpoint", staticConstants.tokensEndpoint);

		String url =
				UrlBuilder.replacePlaceholdersInString("${io-service-url}${tokens-endpoint}", params);
		String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();

		log.info("Save user's {} tokens to IO SERVICE!", user.getId());
		try {
			return restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, TokenDto.class);
		} catch (HttpStatusCodeException e) {
			log.error("Find by token request to IO SERVICE is not 2xx successful!");
			return ResponseEntity.status(e.getStatusCode())
					.headers(e.getResponseHeaders())
					.body(e.getResponseBodyAsString());
		}
	}

	public void revokeToken(String jwtToken) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		Map<String, Object> params = new HashMap<>();
		params.put("io-service-url", staticConstants.ioServiceUrl);
		params.put("tokens-endpoint", staticConstants.tokensEndpoint);
		params.put("token-revoke-endpoint", staticConstants.tokenRevokeEndpoint);
		params.put("token", jwtToken);

		String url =
				UrlBuilder.replacePlaceholdersInString(
						"${io-service-url}${tokens-endpoint}${token-revoke-endpoint}/${token}", params);

		log.info("Revoke token request to IO SERVICE!");

		String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();

		restTemplate.postForEntity(urlTemplate, entity, Object.class);
	}

	public boolean isRefreshToken(String refreshToken) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		Map<String, Object> params = new HashMap<>();
		params.put("io-service-url", staticConstants.ioServiceUrl);
		params.put("tokens-endpoint", staticConstants.tokensEndpoint);
		params.put("is-refresh-token", staticConstants.isRefreshTokenEndpoint);
		params.put("token", refreshToken);

		String url =
				UrlBuilder.replacePlaceholdersInString(
						"${io-service-url}${tokens-endpoint}${is-refresh-token}/${token}", params);

		String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();

		log.info("Checking if refresh token is actually refresh token!");

		try {
			ResponseEntity<Boolean> response =
					restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, Boolean.class);
			return Boolean.TRUE.equals(response.getBody());
		} catch (HttpStatusCodeException e) {
			return false;
		}
	}
}
