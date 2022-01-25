package pe.com.certifakt.apifact.dto;

import lombok.*;
import pe.com.certifakt.apifact.bean.IdentificadorComprobante;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SendBoletaDTO implements Serializable{

	private String ruc;
	private String fechaEmision;
	private IdentificadorComprobante nameDocument;
	private String user;
	private Boolean envioDirecto;
}
