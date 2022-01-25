package pe.com.certifakt.apifact.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeErrorEnum {

	ERROR("ERROR"),
	WARNING("OBSERV");
	private final String type;




}
