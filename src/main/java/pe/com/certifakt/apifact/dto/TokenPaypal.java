package pe.com.certifakt.apifact.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TokenPaypal {
    private String access_token;
}
