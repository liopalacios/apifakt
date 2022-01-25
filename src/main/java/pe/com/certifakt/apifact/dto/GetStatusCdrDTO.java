package pe.com.certifakt.apifact.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GetStatusCdrDTO implements Serializable {

	private String ruc;
	private String tipoComprobante;
	private String serie;
	private Integer numero;
	private Long idPaymentVoucher;
	
}
