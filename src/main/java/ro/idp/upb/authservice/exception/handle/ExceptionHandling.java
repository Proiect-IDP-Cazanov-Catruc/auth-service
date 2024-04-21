/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.exception.handle;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ro.idp.upb.authservice.exception.*;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandling {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorMessage> handleAuthenticationException(
			final AuthenticationException ex, final HttpServletRequest request) {
		ErrorMessage errorMessage =
				buildErrorMessage(HttpStatus.BAD_REQUEST, ErrorCode.E_004, ex, request);
		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ErrorMessage> handleExpiredJwtException(
			ExpiredJwtException ex, HttpServletRequest request) {
		ErrorMessage errorMessage =
				buildErrorMessage(HttpStatus.BAD_REQUEST, ErrorCode.E_007, ex, request);
		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
	}

	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<ErrorMessage> handleSignatureException(
			SignatureException ex, HttpServletRequest request) {
		ErrorMessage errorMessage =
				buildErrorMessage(HttpStatus.BAD_REQUEST, ErrorCode.E_008, ex, request);
		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ErrorMessage> handleInvalidToken(
			InvalidTokenException ex, HttpServletRequest request) {
		ErrorMessage errorMessage =
				buildErrorMessage(HttpStatus.BAD_REQUEST, ErrorCode.E_004, ex, request);
		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
	}

	@ExceptionHandler(MissingTokenException.class)
	public ResponseEntity<ErrorMessage> handleMissingTokenException(
			MissingTokenException ex, HttpServletRequest request) {
		ErrorMessage errorMessage =
				buildErrorMessage(HttpStatus.BAD_REQUEST, ErrorCode.E_105, ex, request);
		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
	}

	@ExceptionHandler(NotRefreshTokenException.class)
	public ResponseEntity<ErrorMessage> handleNotRefreshTokenException(
			NotRefreshTokenException ex, HttpServletRequest request) {
		ErrorMessage errorMessage =
				buildErrorMessage(HttpStatus.BAD_REQUEST, ErrorCode.E_104, ex, request);
		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
	}

	@ExceptionHandler(SecurityContextUsernameException.class)
	public ResponseEntity<ErrorMessage> securityContextUsernameException(
			final SecurityContextUsernameException ex, HttpServletRequest request) {
		ErrorMessage errorMessage =
				buildErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E_003, ex, request);
		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessage> handleValidationErrors(
			MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<ValidationError> validationErrors =
				ex.getBindingResult().getFieldErrors().stream()
						.map(
								fieldError ->
										ValidationError.builder()
												.field(fieldError.getField())
												.message(fieldError.getDefaultMessage())
												.build())
						.toList();

		ErrorMessage errorMessage =
				buildErrorMessage(HttpStatus.BAD_REQUEST, ErrorCode.E_201, ex, request);
		errorMessage.setValidationErrors(validationErrors);
		errorMessage.setDebugMessage(ex.getTypeMessageCode());

		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> exception(Exception ex, HttpServletRequest request) {
		ErrorMessage errorMessage =
				buildErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E_001, ex, request);
		return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(errorMessage.getStatus()));
	}

	private ErrorMessage buildErrorMessage(
			HttpStatus code, ErrorCode errorCode, Exception e, HttpServletRequest request) {
		ErrorMessage error = new ErrorMessage(code, errorCode, e);
		error.setPath(request.getRequestURI());
		return error;
	}

	@ExceptionHandler(RestTemplateException.class)
	public ResponseEntity<ErrorMessage> handleRestTemplateException(
			RestTemplateException e, HttpServletRequest request) {
		ErrorMessage errorMessage = e.getErrorMessage();
		errorMessage.setPath(request.getRequestURI());

		return ResponseEntity.status(HttpStatus.valueOf(errorMessage.getStatus())).body(errorMessage);
	}
}
