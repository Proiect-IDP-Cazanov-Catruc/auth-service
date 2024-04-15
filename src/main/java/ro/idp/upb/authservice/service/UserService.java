/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.security.auth.login.LoginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ro.idp.upb.authservice.config.SecurityUtils;
import ro.idp.upb.authservice.data.dto.request.RegisterRequest;
import ro.idp.upb.authservice.data.dto.response.UserDto;
import ro.idp.upb.authservice.data.entity.User;
import ro.idp.upb.authservice.utils.StaticConstants;
import ro.idp.upb.authservice.utils.UrlBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final StaticConstants staticConstants;

	public Optional<User> findByEmail(String email) {
		RestTemplate restTemplate = new RestTemplate();
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

		ResponseEntity<UserDto> response =
				restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, UserDto.class);
		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error("Unable to find user details by user email {}!", email);
			return Optional.empty();
		} else {
			log.info("Successfully fetched user details by user email {}!", email);
			UserDto dtoResponse = response.getBody();
			return Optional.of(userDtoToEntity(dtoResponse));
		}
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

	public Optional<User> registerUser(RegisterRequest registerRequest) {
		RestTemplate restTemplate = new RestTemplate();
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
				"Register user request to IO SERVICE: email {}, firstName {}, lastName {}!",
				registerRequest.getFirstName(),
				registerRequest.getLastName(),
				registerRequest.getEmail());

		String urlTemplate = UriComponentsBuilder.fromHttpUrl(url).encode().toUriString();

		ResponseEntity<UserDto> response =
				restTemplate.postForEntity(urlTemplate, entity, UserDto.class);
		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error(
					"Unable to register email {}, firstName {}, lastName {}!",
					registerRequest.getEmail(),
					registerRequest.getFirstName(),
					registerRequest.getLastName());
			return Optional.empty();
		} else {
			log.info(
					"Successfully registered user with: email {}, firstName {}, lastName {}!",
					registerRequest.getEmail(),
					registerRequest.getFirstName(),
					registerRequest.getLastName());
			return Optional.of(userDtoToEntity(response.getBody()));
		}
	}

	public UserDto getUserDto() throws LoginException {
		final var username = SecurityUtils.getCurrentUserLogin().orElseThrow(LoginException::new);
		final var user =
				findByEmail(username)
						.orElseThrow(() -> new UsernameNotFoundException("Username not found!"));

		return UserDto.builder()
				.email(user.getEmail())
				.firstName(user.getFirstname())
				.lastName(user.getLastname())
				.role(user.getRole())
				.build();
	}
}
