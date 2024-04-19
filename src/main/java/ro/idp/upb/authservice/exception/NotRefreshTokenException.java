/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.exception;

import java.util.Optional;

public class NotRefreshTokenException extends RuntimeException {
	private final Optional<String> token;

	public NotRefreshTokenException(String token) {
		super();
		this.token = Optional.ofNullable(token);
	}

	public NotRefreshTokenException() {
		super();
		this.token = Optional.empty();
	}

	@Override
	public String getMessage() {
		if (token.isPresent()) {
			return String.format("Provided token %s is not refresh token", token.get());
		}
		return "Provided token is not refresh token";
	}
}
