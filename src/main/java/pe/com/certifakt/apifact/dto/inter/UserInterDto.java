package pe.com.certifakt.apifact.dto.inter;

import javax.persistence.Column;

public interface UserInterDto {
    Integer getId();
    Integer getIdOficina();

    String getUsername();

    String getPassword();

    String getFullName();

    String getDni();

    Boolean getEstado();

    Boolean getEnabled();



}
