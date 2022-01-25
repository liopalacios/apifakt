package pe.com.certifakt.apifact.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SendBillDTO implements Serializable{

	private String ruc;
	private Long idPaymentVoucher;
	private String nameDocument;
	private Boolean envioAutomaticoSunat;

}
