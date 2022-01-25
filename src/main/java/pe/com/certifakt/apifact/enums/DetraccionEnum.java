package pe.com.certifakt.apifact.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DetraccionEnum {

    SI("S"),
    NO("N");
    private final String codigo;


}
