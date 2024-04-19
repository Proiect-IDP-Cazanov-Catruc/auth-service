/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.exception;

public class InvalidTokenException extends RuntimeException {
	public InvalidTokenException() {
		super();
	}

	@Override
	public String getMessage() {
		return "Invalid token provided";
	}
}
