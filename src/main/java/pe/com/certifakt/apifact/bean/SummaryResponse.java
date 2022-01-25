package pe.com.certifakt.apifact.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SummaryResponse  implements Serializable {

	private String fechaEmision;
	private String idDocumento;
	private String ticket;
	private List<Comprobante> comprobantes;

}
