package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.deserializer.PaymentVoucherDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = PaymentVoucherDeserializer.class)
@Builder
public class PaymentVoucher implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String tipoComprobante;
    private String serie;
    private Integer numero;
    private String fechaEmision;
    private String horaEmision;
    private String fechaVencimiento;
    private String codigoMoneda;
    private String codigoTipoOperacion;
    private String codigoTipoOperacionCatalogo51;
    private String rucEmisor;
    private String direccionOficinaEmisor;
    private String codigoLocalAnexoEmisor;
    private String tipoDocumentoReceptor;
    private String numeroDocumentoReceptor;
    private String denominacionReceptor;
    private String direccionReceptor;
    private String emailReceptor;
    private List<GuiaRelacionada> guiasRelacionadas;
    private List<DocumentoRelacionado> documentosRelacionados;
    private List<Leyenda> leyendas;
    private String serieNumeroOtroDocumentoRelacionado;
    private String codigoTipoOtroDocumentoRelacionado;
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
    private BigDecimal totalImpOperGratuita;
    private BigDecimal totalOtrostributos;
    private BigDecimal totalDescuento;
    private BigDecimal descuentoGlobales;
    private BigDecimal sumatoriaOtrosCargos;
    private BigDecimal totalAnticipos;
    private BigDecimal importeTotalVenta;
    private String serieAfectado;
    private Integer numeroAfectado;
    private String tipoComprobanteAfectado;
    private String codigoTipoNotaCredito;
    private String codigoTipoNotaDebito;
    private String motivoNota;
    private List<PaymentVoucherLine> items;
    private String denominacionEmisor;
    private String nombreComercialEmisor;
    private String tipoDocumentoEmisor;
    private String identificadorDocumento;
    private String ordenCompra;
    private List<Anticipo> anticipos;
    private List<CampoAdicional> camposAdicionales;
    private List<PaymentVoucherCuota> cuotas;
    private String codigoBienDetraccion;
    private BigDecimal porcentajeDetraccion;
    private String cuentaFinancieraBeneficiario;
    private String codigoMedioPago;
    private BigDecimal montoDetraccion;
    private String detraccion;
    private String UblVersion;
    private String codigoHash;
    private Integer oficinaId;

    private BigDecimal tipoTransaccion;
    private BigDecimal montoPendiente;
    private BigDecimal cantidadCuotas;
    private BigDecimal pagoCuenta;
/*
    private String numeroCuota;
    private BigDecimal montoCuota;
    private BigDecimal montoPendiente;
    private Integer nCuota;
    private String idPaymentVoucherReference;
*/
}

