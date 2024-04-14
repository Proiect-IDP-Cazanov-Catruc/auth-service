package ro.idp.upb.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ro.idp.upb.authservice.data.dto.request.RegisterRequest;
import ro.idp.upb.authservice.data.dto.response.UserDto;
import ro.idp.upb.authservice.data.entity.User;
import ro.idp.upb.authservice.utils.StaticConstants;
import ro.idp.upb.authservice.utils.UrlBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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

        String url = UrlBuilder.replacePlaceholdersInString(
                "${io-service-url}${users-endpoint}${find-by-email-endpoint}/${email}",
                params
        );

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .encode()
                .toUriString();

        ResponseEntity<UserDto> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                UserDto.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            return Optional.empty();
        } else {
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

        String url = UrlBuilder.replacePlaceholdersInString(
                "${io-service-url}${users-endpoint}${register-endpoint}",
                params
        );

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .encode()
                .toUriString();

        ResponseEntity<UserDto> response = restTemplate.postForEntity(
                urlTemplate,
                entity,
                UserDto.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            return Optional.empty();
        } else {
            return Optional.of(userDtoToEntity(response.getBody()));
        }
    }
}
