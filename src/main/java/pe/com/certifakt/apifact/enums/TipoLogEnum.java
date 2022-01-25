package pe.com.certifakt.apifact.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoLogEnum {

	ERROR("ERROR"),
	WARNING("WARNING"),
	INFO("INFO");
	
	private final String level;

}
