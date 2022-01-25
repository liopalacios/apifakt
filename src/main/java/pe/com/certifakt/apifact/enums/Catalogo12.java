package pe.com.certifakt.apifact.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Catalogo12 {

	FACTURA_ANTICIPOS( "02"),
	BOLETA_ANTICIPOS( "03");
	private final String codigo;

}
