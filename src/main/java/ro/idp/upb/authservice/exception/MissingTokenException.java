/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.exception;

public class MissingTokenException extends RuntimeException {
	public MissingTokenException() {
		super();
	}

	@Override
	public String getMessage() {
		return "Missing bearer token in Authentication header";
	}
}
