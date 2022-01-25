package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anticipo  implements Serializable {

	private String identificadorPago;
	private String serieAnticipo;
	private Integer numeroAnticipo;
	private String tipoDocumentoAnticipo;
	private BigDecimal montoAnticipado;

}
