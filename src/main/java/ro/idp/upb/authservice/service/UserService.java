/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ro.idp.upb.authservice.config.SecurityUtils;
import ro.idp.upb.authservice.data.dto.request.AuthenticationRequest;
import ro.idp.upb.authservice.data.dto.request.RegisterRequest;
import ro.idp.upb.authservice.data.dto.response.UserDto;
import ro.idp.upb.authservice.data.entity.User;
import ro.idp.upb.authservice.exception.handle.RestTemplateResponseErrorHandler;
import ro.idp.upb.authservice.utils.StaticConstants;
import ro.idp.upb.authservice.utils.UrlBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final StaticConstants staticConstants;
	private final ObjectMapper objectMapper;

	public User findByEmail(String email) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(
				new RestTemplateResponseErrorHandler(
						objectMapper, () -> log.error("Unable to find user details by user email {}!", email)));
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);

		Map<String, Object> params = new HashMap<>();
		params.put("io-service-url", staticConstants.ioServiceUrl);
		params.put("users-endpoint", staticConstants.usersEndpoint);
		params.put("find-by-email-endpoint", staticConstants.findByEmailEndpoint);
		params.put("email", email);

		String url =
				UrlBuilder.replacePlaceholdersInString(
						"${io-service-url}${users-endpoint}${find-by-email-endpoint}/${email}", params);

		log.info("Find user details by user email {} request to IO SERVICE!", email);

		String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();

		ResponseEntity<UserDto> response;
		response = restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, UserDto.class);

		log.info("Successfully fetched user details by user email {}!", email);
		UserDto dtoResponse = response.getBody();
		return userDtoToEntity(dtoResponse);
	}

	public User isAuthRequestValid(AuthenticationRequest request) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(
				new RestTemplateResponseErrorHandler(
						objectMapper,
						() -> log.error("Invalid credentials for email {}!", request.getEmail())));
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(request, headers);

		Map<String, Object> params = new HashMap<>();
		params.put("io-service-url", staticConstants.ioServiceUrl);
		params.put("users-endpoint", staticConstants.usersEndpoint);
		params.put("validate-login-endpoint", staticConstants.validateLoginEndpoint);

		String url =
				UrlBuilder.replacePlaceholdersInString(
						"${io-service-url}${users-endpoint}${validate-login-endpoint}", params);

		log.info("Delegate validate login request for email {} to IO SERVICE!", request.getEmail());

		ResponseEntity<UserDto> response;
		response = restTemplate.exchange(url, HttpMethod.POST, entity, UserDto.class);
		log.info("Login request valid for email {}!", request.getEmail());
		return userDtoToEntity(response.getBody());
	}

	public User userDtoToEntity(UserDto userDto) {
		return User.builder()
				.id(userDto.getId())
				.firstname(userDto.getFirstName())
				.lastname(userDto.getLastName())
				.email(userDto.getEmail())
				.role(userDto.getRole())
				.build();
	}

	public User registerUser(RegisterRequest registerRequest) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(
				new RestTemplateResponseErrorHandler(
						objectMapper,
						() ->
								log.error(
										"Register request for [Firstname: {}], [Lastname: {}], [Email: {}] went wrong!",
										registerRequest.getFirstName(),
										registerRequest.getLastName(),
										registerRequest.getEmail())));
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(registerRequest, headers);

		Map<String, Object> params = new HashMap<>();
		params.put("io-service-url", staticConstants.ioServiceUrl);
		params.put("users-endpoint", staticConstants.usersEndpoint);
		params.put("register-endpoint", staticConstants.registerEndpoint);

		String url =
				UrlBuilder.replacePlaceholdersInString(
						"${io-service-url}${users-endpoint}${register-endpoint}", params);

		log.info(
				"Register user request to IO SERVICE [Firstname: {}], [Lastname: {}], [Email: {}]!",
				registerRequest.getFirstName(),
				registerRequest.getLastName(),
				registerRequest.getEmail());

		String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();

		ResponseEntity<UserDto> response;

		response = restTemplate.postForEntity(urlTemplate, entity, UserDto.class);

		log.info(
				"Successfully registered user [Firstname: {}], [Lastname: {}], [Email: {}]!",
				registerRequest.getFirstName(),
				registerRequest.getLastName(),
				registerRequest.getEmail());
		return userDtoToEntity(response.getBody());
	}

	public UserDto getUserDto() {
		final var username = SecurityUtils.getCurrentUserLogin();
		final var user = findByEmail(username);

		return UserDto.builder()
				.id(user.getId())
				.email(user.getEmail())
				.firstName(user.getFirstname())
				.lastName(user.getLastname())
				.role(user.getRole())
				.build();
	}
}
