/* Ionel Catruc 343C3, Veaceslav Cazanov 343C3 | IDP AUTH-SERVICE | (C) 2024 */
package ro.idp.upb.authservice.data.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.idp.upb.authservice.data.enums.TokenType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
	public UUID id;
	public String token;
	public TokenType tokenType;
	public boolean revoked;
	public boolean expired;
	public Token associatedToken;
	public UUID userId;
}
