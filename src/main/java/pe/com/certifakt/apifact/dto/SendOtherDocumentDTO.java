package pe.com.certifakt.apifact.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SendOtherDocumentDTO implements Serializable{

	private String uuidSaved;
	private Long idVoucher;
	private String nameDocument;
	private String ruc;
	private String tipoComprobante;
	private Integer numeroBaja;
	private String serieBaja;

    private Boolean envioAutomaticoSunat;

}
