package ro.idp.upb.authservice.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.idp.upb.authservice.data.enums.TokenType;

import java.util.UUID;

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