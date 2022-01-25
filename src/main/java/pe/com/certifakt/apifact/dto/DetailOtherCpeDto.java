package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailOtherCpeDto {

    private Long idDetailOtherCpe;

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
    private String estado;
}
