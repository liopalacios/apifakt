package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.model.OtherCpeEntity;
import pe.com.certifakt.apifact.model.OtherCpeFileEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtherCpeDto {

    private Long idOtroCPE;
    private String serie;
    private Integer numero;
    private String fechaEmision;
    private Date fechaEmisionDate;
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

    private String ubigeoDomicilioFiscalReceptor;
    private String direccionCompletaDomicilioFiscalReceptor;
    private String urbanizacionDomicilioFiscalReceptor;
    private String departamentoDomicilioFiscalReceptor;
    private String provinciaDomicilioFiscalReceptor;
    private String distritoDomicilioFiscalReceptor;
    private String codigoPaisDomicilioFiscalReceptor;

    private String emailReceptor;
    private String regimen;
    private BigDecimal tasa;
    private String observaciones;
    private BigDecimal importeTotalRetenidoPercibido;
    private BigDecimal importeTotalPagadoCobrado;
    private BigDecimal montoRedondeoImporteTotal;
    private String codigoMoneda;
    private String identificadorDocumento;
    private String estado;
    private String estadoAnterior;
    private String estadoEnSunat;
    private String mensajeRespuesta;
    private Timestamp fechaRegistro;
    private Timestamp fechaModificacion;
    private String userName;
    private String userNameModify;

    private List<DetailOtherCpeDto> details;

    private List<OtherCpeFileEntity> otherCpeFiles = new ArrayList<>();
    private String uuid;

    public static OtherCpeDto transformToDtoLite(OtherCpeEntity model){
        if (model == null) return null;
        return OtherCpeDto.builder()
                .idOtroCPE(model.getIdOtroCPE())
                .identificadorDocumento(model.getIdentificadorDocumento())
                .fechaEmision(model.getFechaEmision())
                .fechaRegistro(model.getFechaRegistro())
                .tipoComprobante(model.getTipoComprobante())
                .numero(model.getNumero())
                .serie(model.getSerie())
                .denominacionEmisor(model.getDenominacionEmisor())
                .numeroDocumentoIdentidadEmisor(model.getNumeroDocumentoIdentidadEmisor())
                .numeroDocumentoIdentidadReceptor(model.getNumeroDocumentoIdentidadReceptor())
                .denominacionReceptor(model.getDenominacionReceptor())
                .importeTotalRetenidoPercibido(model.getImporteTotalRetenidoPercibido())
                .importeTotalPagadoCobrado(model.getImporteTotalPagadoCobrado())
                .estado(model.getEstado())
                .estadoEnSunat(model.getEstadoEnSunat())
                .mensajeRespuesta(model.getMensajeRespuesta())
                .uuid(model.getUuid())
                //  .bienesToTransportar(GuiaItemDto.transformToDtoListLite(model.getDetailsGuiaRemision()))
                .build();
    }
}
