package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.bean.PaymentVoucher;
import pe.com.certifakt.apifact.config.UnixTimestampDateSerializer;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.util.UUIDGen;

import javax.persistence.*;
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
public class PaymentVoucherDto {
    private Long idPaymentVoucher;
    private String serie;
    private Integer numero;
    private String fechaEmision;
    private Date fechaEmisionDate;
    private String horaEmision;
    private String tipoComprobante;
    private String codigoMoneda;
    private String fechaVencimiento;
    private String tipoOperacion;
    private String rucEmisor;
    private String codigoLocalAnexo;
    private String tipoDocIdentReceptor;
    private String numDocIdentReceptor;
    private String denominacionReceptor;
    private String direccionReceptor;
    private String emailReceptor;
    private List<AditionalFieldEntity> aditionalFields = new ArrayList<>();
    private String codigoTipoDocumentoRelacionado;
    private String serieNumeroDocumentoRelacionado;
    private BigDecimal totalValorVentaOperacionExportada;
    private BigDecimal totalValorVentaOperacionGravada;
    private BigDecimal totalValorVentaOperacionInafecta;
    private BigDecimal totalValorVentaOperacionExonerada;
    private BigDecimal totalValorVentaOperacionGratuita;
    private BigDecimal totalValorBaseIsc;
    private BigDecimal totalValorBaseOtrosTributos;
    private BigDecimal totalValorVentaGravadaIVAP;
    private BigDecimal totalDescuento;
    private BigDecimal sumatoriaTributosOperacionGratuita;
    private BigDecimal sumatoriaIvap;
    private BigDecimal sumatoriaIGV;
    private BigDecimal sumatoriaISC;
    private BigDecimal sumatoriaOtrosTributos;
    private BigDecimal montoDescuentoGlobal;
    private BigDecimal montoSumatorioOtrosCargos;
    private BigDecimal montoImporteTotalVenta;
    private BigDecimal montoTotalAnticipos;
    //Usado para Nota Credito/Debito
    private String serieAfectado;
    private Integer numeroAfectado;
    private String tipoComprobanteAfectado;
    private String motivoNota;
    private String codigoTipoNotaCredito;
    private String codigoTipoNotaDebito;
    private String identificadorDocumento;
    private Integer estadoItem;
    private String estadoSunat;
    private String estado;
    private String estadoAnterior;
    private String mensajeRespuesta;
    private Timestamp fechaRegistro;
    private Timestamp fechaModificacion;
    private String userName;
    private String userNameModify;
    private String motivoAnulacion;
    private List<DetailsPaymentVoucherDto> detailsPaymentVouchers;
    private List<AnticipoEntity> anticipos;
    private List<GuiaRelacionadaEntity> guiasRelacionadas;
    private String uuid;
    private String ordenCompra;
    private Integer estadoAnticipo;
    private String codigosRespuestaSunat;
    private String ublVersion;
    private Boolean boletaAnuladaSinEmitir;
    private List<PaymentVoucherFileEntity> paymentVoucherFiles = new ArrayList<>();
    private String codigoHash;
    private void prePersist() {
        this.uuid = UUIDGen.generate();
    }
    private String codigoMedioPago;
    private String cuentaFinancieraBeneficiario;
    private String codigoBienDetraccion;
    private BigDecimal porcentajeDetraccion;
    private BigDecimal montoDetraccion;
    private String detraccion;

    private BranchOfficeEntity oficina;

    private String identificadorBaja;

    public static PaymentVoucherDto transformToDtoLite(PaymentVoucherEntity model){
        if (model == null) return null;
        return PaymentVoucherDto.builder()
                .idPaymentVoucher(model.getIdPaymentVoucher())
                .identificadorDocumento(model.getIdentificadorDocumento())
                .fechaEmision(model.getFechaEmision())
                .fechaRegistro(model.getFechaRegistro())
                .tipoComprobante(model.getTipoComprobante())
                .tipoComprobanteAfectado(model.getTipoComprobanteAfectado())
                .numero(model.getNumero())
                .serie(model.getSerie())
                .numDocIdentReceptor(model.getNumDocIdentReceptor())
                .denominacionReceptor(model.getDenominacionReceptor())
                .codigoMoneda(model.getCodigoMoneda())
                .montoImporteTotalVenta(model.getMontoImporteTotalVenta())
                .estado(model.getEstado())
                .estadoSunat(model.getEstadoSunat())
                .mensajeRespuesta(model.getMensajeRespuesta())
                .rucEmisor(model.getRucEmisor())
                .identificadorBaja(model.getIdentificadorBaja())
                .emailReceptor(model.getEmailReceptor())
                .uuid(model.getUuid())
                .ublVersion(model.getUblVersion())
                .detailsPaymentVouchers(DetailsPaymentVoucherDto.transformToDtoListLite( model.getDetailsPaymentVouchers()))
                .build();
    }

}
