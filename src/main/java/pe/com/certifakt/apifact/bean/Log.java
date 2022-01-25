package pe.com.certifakt.apifact.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Log  implements Serializable {

	private String tipoLog;
	private String rucEmisor;
	private String identificadorDocumento;
	private String operacion;
	private String subOperacion;
	private String parametros;
	private String nombreClase;
	private String nombreMetodo;
	private String mensaje;
	private Throwable excepcion;

}
