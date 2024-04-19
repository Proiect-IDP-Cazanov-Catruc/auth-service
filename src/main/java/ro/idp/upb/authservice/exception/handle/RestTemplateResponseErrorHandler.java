/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.exception.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import ro.idp.upb.authservice.exception.RestTemplateException;

public class RestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {

	private final ObjectMapper objectMapper;
	private final RestTemplateErrorHandlingLog restTemplateErrorHandlingLog;

	public RestTemplateResponseErrorHandler(
			final ObjectMapper objectMapper,
			final RestTemplateErrorHandlingLog restTemplateErrorHandlingLog) {
		this.objectMapper = objectMapper;
		this.restTemplateErrorHandlingLog = restTemplateErrorHandlingLog;
	}

	@Override
	protected void handleError(
			@NonNull ClientHttpResponse response, @NonNull HttpStatusCode statusCode) throws IOException {
		byte[] originBody = getResponseBody(response);
		String originBodyString = new String(originBody);
		restTemplateErrorHandlingLog.logErrorMessage();

		ErrorMessage errorMessage = objectMapper.readValue(originBodyString, ErrorMessage.class);
		throw new RestTemplateException(errorMessage);
	}
}
