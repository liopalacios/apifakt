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
public class VoidedLine  implements Serializable {

	private Integer numeroItem;
	private Integer numeroDocumento;
	private String serieDocumento;
	private String tipoComprobante;
	private String razon;

}
