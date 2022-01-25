package pe.com.certifakt.apifact.bean;

import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String userName;
    private String fullName;
    private String typeUser;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String ublVersion;

}
