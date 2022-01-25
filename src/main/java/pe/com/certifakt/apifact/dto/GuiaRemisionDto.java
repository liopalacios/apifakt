package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.bean.CampoAdicionalGuia;
import pe.com.certifakt.apifact.bean.GuiaItem;
import pe.com.certifakt.apifact.bean.TramoTraslado;
import pe.com.certifakt.apifact.model.AditionalFieldGuiaEntity;
import pe.com.certifakt.apifact.model.GuiaRemisionEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuiaRemisionDto {

    private Long idGuiaRemision;
    private String serie;
    private Integer numero;
    private String fechaEmision;
    private String horaEmision;
    private String tipoComprobante;
    private String serieGuiaBaja;
    private Integer numeroGuiaBaja;
    private String tipoComprobanteBaja;
    private String descripcionComprobanteBaja;
    private String numeracionDAM;
    private String numeracionManifiestoCarga;
    private String identificadorDocumentoRelacionado;
    private String codigoTipoDocumentoRelacionado;
    private String numeroDocumentoIdentidadRemitente;
    private String tipoDocumentoIdentidadRemitente;
    private String denominacionRemitente;
    private String numeroDocumentoIdentidadDestinatario;
    private String tipoDocumentoIdentidadDestinatario;
    private String denominacionDestinatario;
    private String numeroDocumentoIdentidadProveedor;
    private String tipoDocumentoIdentidadProveedor;
    private String denominacionProveedor;
    private String motivoTraslado;
    private String descripcionMotivoTraslado;
    private Boolean indicadorTransbordoProgramado;
    private BigDecimal pesoTotalBrutoBienes;
    private String unidadMedidaPesoBruto;
    private Long numeroBultos;
    private List<TramoTraslado> tramosTraslados;
    private String ubigeoPuntoLlegada;
    private String direccionPuntoLlegada;
    private String numeroContenedor;
    private String ubigeoPuntoPartida;
    private String direccionPuntoPartida;
    private String codigoPuerto;
    private String identificadorDocumento;
    private List<GuiaItemDto> bienesToTransportar;
    private List<AditionalFieldGuiaEntity> aditionalFields = new ArrayList<>();
    private Timestamp fechaRegistro;
    private String mensajeRespuesta;
    private BigDecimal totalValorVentaExportacion;
    private BigDecimal totalValorVentaGravada;
    private BigDecimal totalValorVentaGravadaIVAP;
    private BigDecimal totalValorVentaInafecta;
    private BigDecimal totalValorVentaExonerada;
    private BigDecimal totalValorVentaGratuita;
    private BigDecimal totalValorBaseOtrosTributos;
    private BigDecimal totalValorBaseIsc;
    private BigDecimal totalIgv;
    private BigDecimal totalIvap;
    private BigDecimal totalIsc;
    private String estado;
    private String estadoEnSunat;
    private BigDecimal totalImpOperGratuita;
    private BigDecimal totalOtrostributos;
    private BigDecimal totalDescuento;
    private BigDecimal descuentoGlobales;
    private BigDecimal sumatoriaOtrosCargos;
    private BigDecimal totalAnticipos;
    private BigDecimal importeTotalVenta;
    private BigDecimal montoImporteTotalVenta;
    private String uuid;

    public static GuiaRemisionDto transformToDtoLite(GuiaRemisionEntity model){
        if (model == null) return null;
        return GuiaRemisionDto.builder()
                .idGuiaRemision(model.getIdGuiaRemision())
                .identificadorDocumento(model.getIdentificadorDocumento())
                .fechaEmision(model.getFechaEmision())
                .fechaRegistro(model.getFechaRegistro())
                .tipoComprobante(model.getTipoComprobante())
                .numero(model.getNumero())
                .serie(model.getSerie())
                .denominacionRemitente(model.getDenominacionRemitente())
                .numeroDocumentoIdentidadRemitente(model.getNumeroDocumentoIdentidadRemitente())
                .numeroDocumentoIdentidadDestinatario(model.getNumeroDocumentoIdentidadDestinatario())
                .denominacionDestinatario(model.getDenominacionDestinatario())
                .montoImporteTotalVenta(model.getMontoImporteTotalVenta())
                .estado(model.getEstado())
                .unidadMedidaPesoBruto(model.getUnidadMedidaPesoBruto())
                .estadoEnSunat(model.getEstadoEnSunat())
                .pesoTotalBrutoBienes(model.getPesoTotalBrutoBienes())
                .mensajeRespuesta(model.getMensajeRespuesta())
                .uuid(model.getUuid())
              //  .bienesToTransportar(GuiaItemDto.transformToDtoListLite(model.getDetailsGuiaRemision()))
                .build();
    }

}
