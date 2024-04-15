/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StaticConstants {

	public final String ioServiceUrl;
	public final String usersEndpoint;
	public final String findByEmailEndpoint;
	public final String registerEndpoint;

	public final String tokensEndpoint;
	public final String tokenLogoutEndpoint;
	public final String tokenRevokeEndpoint;
	public final String isRefreshTokenEndpoint;

	public StaticConstants(
			@Value("${idp.io-service.url}") String ioServiceUrl,
			@Value("${idp.io-service.users-endpoint}") String usersEndpoint,
			@Value("${idp.io-service.find-by-email-endpoint}") String findByEmailEndpoint,
			@Value("${idp.io-service.register-endpoint}") String registerEndpoint,
			@Value("${idp.io-service.tokens-endpoint}") String tokensEndpoint,
			@Value("${idp.io-service.token-logout-endpoint}") String tokenLogoutEndpoint,
			@Value("${idp.io-service.token-revoke-endpoint}") String tokenRevokeEndpoint,
			@Value("${idp.io-service.is-refresh-token-endpoint}") String isRefreshTokenEndpoint) {
		this.ioServiceUrl = ioServiceUrl;
		this.usersEndpoint = usersEndpoint;
		this.findByEmailEndpoint = findByEmailEndpoint;
		this.registerEndpoint = registerEndpoint;
		this.tokensEndpoint = tokensEndpoint;
		this.tokenLogoutEndpoint = tokenLogoutEndpoint;
		this.tokenRevokeEndpoint = tokenRevokeEndpoint;
		this.isRefreshTokenEndpoint = isRefreshTokenEndpoint;
	}
}
