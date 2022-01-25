package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.deserializer.DocumentosRelacionadosDeserializer;
import pe.com.certifakt.apifact.deserializer.GuiaItemDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = DocumentosRelacionadosDeserializer.class)
public class DocumentCpe  implements Serializable {

	private String tipoDocumentoRelacionado;
	private String serieDocumentoRelacionado;
	private Integer numeroDocumentoRelacionado;
	private String fechaEmisionDocumentoRelacionado;
	private BigDecimal importeTotalDocumentoRelacionado;
	private String monedaDocumentoRelacionado;

	private String fechaPagoCobro;
	private String numeroPagoCobro;
	private BigDecimal importePagoSinRetencionCobro;
	private String monedaPagoCobro;

	private BigDecimal importeRetenidoPercibido;
	private String monedaImporteRetenidoPercibido;
	private String fechaRetencionPercepcion;
	private BigDecimal importeTotalToPagarCobrar;
	private String monedaImporteTotalToPagarCobrar;

	private String monedaReferenciaTipoCambio;
	private String monedaObjetivoTasaCambio;
	private BigDecimal tipoCambio;
	private String fechaCambio;

}
