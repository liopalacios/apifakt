package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfoEstadoSunat {

	private Long id;
	private String estado;
	private String estadoSunat;

	
}
