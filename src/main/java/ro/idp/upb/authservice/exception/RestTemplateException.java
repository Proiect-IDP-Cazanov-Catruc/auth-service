/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.exception;

import ro.idp.upb.authservice.exception.handle.ErrorMessage;

public class RestTemplateException extends RuntimeException {

	private final ErrorMessage errorMessage;

	public RestTemplateException(final ErrorMessage errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public ErrorMessage getErrorMessage() {
		return errorMessage;
	}
}
