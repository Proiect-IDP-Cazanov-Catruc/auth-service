package ro.idp.upb.authservice.data.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostTokensDto {
    private UUID userId;
    private String accessToken;
    private String refreshToken;
}
