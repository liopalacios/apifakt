package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.deserializer.OtherDocumentCpeDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = OtherDocumentCpeDeserializer.class)
public class OtherDocumentCpe implements Serializable {

    private String serie;
    private Integer numero;
    private String fechaEmision;
    private String horaEmision;
    private String tipoComprobante;

    private String numeroDocumentoIdentidadEmisor;
    private String tipoDocumentoIdentidadEmisor;
    private String nombreComercialEmisor;
    private String denominacionEmisor;

    private String ubigeoDomicilioFiscalEmisor;
    private String direccionCompletaDomicilioFiscalEmisor;
    private String urbanizacionDomicilioFiscalEmisor;
    private String departamentoDomicilioFiscalEmisor;
    private String provinciaDomicilioFiscalEmisor;
    private String distritoDomicilioFiscalEmisor;
    private String codigoPaisDomicilioFiscalEmisor;

    private String numeroDocumentoIdentidadReceptor;
    private String tipoDocumentoIdentidadReceptor;
    private String nombreComercialReceptor;
    private String denominacionReceptor;
    private String emailReceptor;

    private String ubigeoDomicilioFiscalReceptor;
    private String direccionCompletaDomicilioFiscalReceptor;
    private String urbanizacionDomicilioFiscalReceptor;
    private String departamentoDomicilioFiscalReceptor;
    private String provinciaDomicilioFiscalReceptor;
    private String distritoDomicilioFiscalReceptor;
    private String codigoPaisDomicilioFiscalReceptor;

    private String regimen;
    private BigDecimal tasa;
    private String observaciones;
    private BigDecimal importeTotalRetenidoPercibido;
    private BigDecimal importeTotalPagadoCobrado;
    private BigDecimal montoRedondeoImporteTotal;
    private String codigoMoneda;

    private List<DocumentCpe> documentosRelacionados;

}
