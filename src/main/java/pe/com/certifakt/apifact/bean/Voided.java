package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Voided  implements Serializable {

	private String fechaBaja;
	private String rucEmisor;
	private List<VoidedLine> lines;

	private String id;
	private Integer correlativoGeneracionDia;
	private String fechaGeneracion;

	private String denominacionEmisor;
	private String nombreComercialEmisor;
	private String tipoDocumentoEmisor;

	private String estadoComprobante;
}
