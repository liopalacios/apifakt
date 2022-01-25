package pe.com.certifakt.apifact.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoPdfEnum {

	A4("a4"),
	TICKET("ticket"),
	OTHER_CPE("other_cpe"),
	GUIA("guia"),
	GUIA_TICKET("guia-ticket");
	private final String tipo;
}
