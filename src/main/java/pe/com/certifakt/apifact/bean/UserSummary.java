package pe.com.certifakt.apifact.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSummary {


    private Long id;
    private String userName;
    private String fullName;
    private String typeUser;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String ublVersion;
    private boolean guias;
    private Collection<? extends GrantedAuthority> authorities;


}
