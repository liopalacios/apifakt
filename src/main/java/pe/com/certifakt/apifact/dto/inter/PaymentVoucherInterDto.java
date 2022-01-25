package pe.com.certifakt.apifact.dto.inter;

import java.math.BigDecimal;
import java.util.Date;

public interface PaymentVoucherInterDto {



    String getEmisor();
    String getRazon();
    String getEmail();
    String getIdentificador();

    String getDenoReceptor();
    BigDecimal getMontoImporte();

    String getFechaEmision();
    String getTipoComprobante();
    String getSerie();
    Integer getNumero();
    String getTipoDocIdentReceptor();
    String getNumDocIdentReceptor();
    String getDenominacionReceptor();
    String getCodigoMoneda();
    BigDecimal getTotalValorVentaOperacionGravada();
    BigDecimal getTotalValorVentaOperacionExonerada();
    BigDecimal getTotalValorVentaOperacionInafecta();
    BigDecimal getMontoImporteTotalVenta();
    String getEstado();
    String getEstadoSunat();
    BigDecimal getMontoDescuentoGlobal();
    BigDecimal getSumatoriaIGV();

    String getTipoComprobanteAfectado();
    String getSerieAfectado();
    Integer getNumeroAfectado();

    Date getFechaRegistro();
    String getRucEmisor();

    String getIdentificadorBaja();
    String getEmailReceptor();
    Long getId();
    Long getIdPaymentVoucher();

    String getIdentificadorDocumento();
    String getUuid();

    String getCodigoProducto();
    String getDescripcion();
    BigDecimal getCantidad();
    BigDecimal getValorUnitario();
    BigDecimal getValorVenta();
    BigDecimal getDescuento();
    String getTipo();

    String getMensajeRespuesta();
}
