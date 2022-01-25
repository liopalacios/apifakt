package pe.com.certifakt.apifact.dto.inter;

import java.math.BigDecimal;

public interface DetailsPaymentInterDto {
    Integer getIdPayment();
    String getDescripcion();
    String getCodigoUnidadMedida();
    Integer getCantidad();
    BigDecimal getPrecioVentaUnitario();
    BigDecimal getDescuento();
    BigDecimal getValorVenta();
    BigDecimal getAfectacionIGV();

}
