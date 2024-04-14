package ro.idp.upb.authservice.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.idp.upb.authservice.data.enums.TokenType;

import java.util.UUID;

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

