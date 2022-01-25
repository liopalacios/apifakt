package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.deserializer.GuiaItemDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = GuiaItemDeserializer.class)
public class GuiaItem implements Serializable {

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
    private BigDecimal impuestoVentaGratuita;
    private BigDecimal otrosTributos;
    private BigDecimal porcentajeIgv;
    private BigDecimal porcentajeIvap;
    private BigDecimal porcentajeIsc;
    private BigDecimal porcentajeOtrosTributos;
    private BigDecimal porcentajeTributoVentaGratuita;
    private String codigoTipoAfectacionIGV;
    private String codigoTipoCalculoISC;

}
