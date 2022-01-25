package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.config.UnixTimestampDateSerializer;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType;
    @JsonSerialize(using = UnixTimestampDateSerializer.class)
    private Date expires;
    private UserSummary user;
    private List<String> roles;


}
