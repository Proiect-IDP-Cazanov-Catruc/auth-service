/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.data.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.idp.upb.authservice.data.enums.TokenType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
	private UUID id;
	private String token;
	private TokenType tokenType;
	private Boolean revoked;
	private Boolean expired;
	private TokenDto associatedToken;
	private UUID userId;
}
