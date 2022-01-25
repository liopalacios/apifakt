package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseStorage  implements Serializable {

	private String codigoRespuesta;
	private String mensajeRespuesta;
	private Long correlativo;
	private String uuidSaved;
    private Long idRegisterFileUpload;

}

