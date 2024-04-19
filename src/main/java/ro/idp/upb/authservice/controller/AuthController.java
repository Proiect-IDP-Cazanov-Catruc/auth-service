/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.idp.upb.authservice.data.dto.request.AuthenticationRequest;
import ro.idp.upb.authservice.data.dto.request.RegisterRequest;
import ro.idp.upb.authservice.data.dto.response.AuthenticationResponse;
import ro.idp.upb.authservice.service.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationService authService;

	@PostMapping("/register")
	public AuthenticationResponse register(@RequestBody @Valid RegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/authenticate")
	public AuthenticationResponse authenticate(@RequestBody @Valid AuthenticationRequest request) {
		return authService.authenticate(request);
	}

	@PostMapping("/refresh-token")
	public AuthenticationResponse refreshToken(HttpServletRequest request) {
		return authService.refreshToken(request);
	}
}
