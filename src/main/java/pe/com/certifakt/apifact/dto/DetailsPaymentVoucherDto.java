package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.model.DetailsPaymentVoucherEntity;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailsPaymentVoucherDto {
    private Long idDetailsPayment;
    private Integer numeroItem;
    private BigDecimal cantidad;
    private String codigoUnidadMedida;
    private String descripcion;
    private String codigoProductoSunat;
    private String codigoProducto;
    private String codigoProductoGS1;
    private BigDecimal valorUnitario;
    private BigDecimal precioVentaUnitario;
    private BigDecimal valorReferencialUnitario;
    private BigDecimal montoBaseIgv;
    private BigDecimal montoBaseIvap;
    private BigDecimal montoBaseExportacion;
    private BigDecimal montoBaseExonerado;
    private BigDecimal montoBaseInafecto;
    private BigDecimal montoBaseGratuito;
    private BigDecimal montoBaseIsc;
    private BigDecimal montoBaseOtrosTributos;
    private BigDecimal tributoVentaGratuita;
    private BigDecimal otrosTributos;
    private BigDecimal ivap;
    private BigDecimal montoIcbper;
    private BigDecimal montoBaseIcbper;
    private BigDecimal afectacionIGV;
    private BigDecimal sistemaISC;
    private BigDecimal porcentajeIgv;
    private BigDecimal porcentajeIvap;
    private BigDecimal porcentajeIsc;
    private BigDecimal porcentajeOtrosTributos;
    private BigDecimal porcentajeTributoVentaGratuita;
    private String codigoTipoSistemaISC;
    private String codigoTipoAfectacionIGV;
    private BigDecimal valorVenta;
    private BigDecimal descuento;
    private String codigoDescuento;
    private String estado;
    public static DetailsPaymentVoucherDto transformToDtoLite(DetailsPaymentVoucherEntity model){
        if (model == null) return null;
        return DetailsPaymentVoucherDto.builder()
                .idDetailsPayment(model.getIdDetailsPayment())
                .descripcion(model.getDescripcion())
                .codigoUnidadMedida(model.getCodigoUnidadMedida())
                .cantidad(model.getCantidad())
                .precioVentaUnitario(model.getPrecioVentaUnitario())
                .descuento(model.getDescuento())
                .valorVenta(model.getValorVenta())
                .afectacionIGV(model.getAfectacionIGV())
                .build();
    }
    public static List<DetailsPaymentVoucherDto> transformToDtoListLite(List<DetailsPaymentVoucherEntity> models){
        if (models == null) return Collections.emptyList();
        return models.stream().map(DetailsPaymentVoucherDto::transformToDtoLite).collect(Collectors.toList());
    }
}
