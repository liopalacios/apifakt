package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentoRelacionado {

	private String numero;
	private String tipoDocumento;	
}
