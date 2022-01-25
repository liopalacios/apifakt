package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.model.DetailGuiaRemisionEntity;
import pe.com.certifakt.apifact.model.DetailsPaymentVoucherEntity;

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
public class GuiaItemDto {

    private Long idDetailGuiaRemision;
    private Integer numeroOrden;
    private BigDecimal cantidad;
    private String unidadMedida;
    private String descripcion;
    private String codigoItem;
    private BigDecimal precioItem;
    /**/
    private String codigoProductoSunat;
    private String codigoProductoGS1;
    private BigDecimal valorUnitario;
    private BigDecimal descuento;
    private String codigoDescuento;
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
    private BigDecimal igv;
    private BigDecimal isc;
    private BigDecimal ivap;
    private String codigoUnidadMedida;
    private BigDecimal impuestoVentaGratuita;
    private BigDecimal otrosTributos;
    private BigDecimal porcentajeIgv;
    private BigDecimal porcentajeIvap;
    private BigDecimal porcentajeIsc;
    private BigDecimal porcentajeOtrosTributos;
    private BigDecimal porcentajeTributoVentaGratuita;
    private String codigoTipoAfectacionIGV;
    private String codigoTipoCalculoISC;
    private BigDecimal afectacionIGV;

    public static GuiaItemDto transformToDtoLite(DetailGuiaRemisionEntity model){
        if (model == null) return null;
        return GuiaItemDto.builder()
                .idDetailGuiaRemision(model.getIdDetailGuiaRemision())
                .descripcion(model.getDescripcion())
                .codigoUnidadMedida(model.getCodigoUnidadMedida())
                .cantidad(model.getCantidad())
                .precioVentaUnitario(model.getPrecioVentaUnitario())
                .descuento(model.getDescuento())
                .precioItem(model.getPrecioItem())
                .afectacionIGV(model.getAfectacionIGV())
                .build();
    }
    public static List<GuiaItemDto> transformToDtoListLite(List<DetailGuiaRemisionEntity> models){
        if (models == null) return Collections.emptyList();
        return models.stream().map(GuiaItemDto::transformToDtoLite).collect(Collectors.toList());
    }
}
