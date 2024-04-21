/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.config;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import ro.idp.upb.authservice.exception.SecurityContextUsernameException;

@Slf4j
public final class SecurityUtils {

	public static String getCurrentUserLogin() {
		var securityContext = SecurityContextHolder.getContext();
		return Optional.ofNullable(securityContext.getAuthentication())
				.map(
						authentication -> {
							if (authentication.getPrincipal() instanceof final UserDetails springSecurityUser) {
								return springSecurityUser.getUsername();
							}
							if (authentication.getPrincipal() instanceof final String authenticationPrincipal) {
								return authenticationPrincipal;
							}
							return null;
						})
				.orElseThrow(
						() -> {
							log.error("Unable to get current user login!");
							return new SecurityContextUsernameException();
						});
	}

	public static boolean isAuthenticated() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.isAuthenticated();
	}

	public static boolean isCurrentUserInRole(final String role) {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null
				&& getAuthorities(authentication).anyMatch(role::equalsIgnoreCase);
	}

	private static Stream<String> getAuthorities(final Authentication authentication) {
		return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
	}
}
