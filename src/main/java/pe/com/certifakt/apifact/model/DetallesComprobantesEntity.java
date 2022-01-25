package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the details_payment_voucher database table.
 *
 */
@Entity
@Table(name="details_payment_voucher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetallesComprobantesEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="detail_payment_voucher_seq", sequenceName = "detail_payment_voucher_seq", allocationSize=1 )
    @GeneratedValue(strategy= GenerationType.AUTO, generator="detail_payment_voucher_seq")
    @Column(name="id_details_payment")
    private Long id_details_payment;

    private ComprobantesEntity payment_voucher;

    @Column(name="numero_item", nullable=false)
    private Integer numero_item;
    @Column(name="cantidad", precision=12, scale=2)
    private BigDecimal cantidad;
    @Column(name="cod_unid_medida", length=3)
    private String cod_unid_medida;

    @Column(name="descripcion_producto", length=500)
    private String descripcion_producto;
    @Column(name="cod_prod_sunat", length=20)
    private String cod_prod_sunat;
    @Column(name="cod_producto", length=30)
    private String cod_producto;
    @Column(name="cod_producto_gs1", length=20)
    private String cod_producto_gs1;

    @Column(name="valor_unit", precision=35, scale=20)
    private BigDecimal valor_unit;

    @Column(name="precio_venta_unit", precision=35, scale=20)
    private BigDecimal precio_venta_unit;
    @Column(name="valor_referencial_unit", precision=35, scale=20)
    private BigDecimal valor_referencial_unit;

    @Column(name="monto_base_igv", precision=35, scale=20)
    private BigDecimal monto_base_igv;
    @Column(name="monto_base_ivap", precision=35, scale=20)
    private BigDecimal monto_base_ivap;
    @Column(name="monto_base_export", precision=35, scale=20)
    private BigDecimal monto_base_export;
    @Column(name="monto_base_exone", precision=35, scale=20)
    private BigDecimal monto_base_exone;
    @Column(name="monto_base_inafec", precision=35, scale=20)
    private BigDecimal monto_base_inafec;
    @Column(name="monto_base_grat", precision=35, scale=20)
    private BigDecimal monto_base_grat;
    @Column(name="monto_base_isc", precision=35, scale=20)
    private BigDecimal monto_base_isc;
    @Column(name="monto_base_otros", precision=35, scale=20)
    private BigDecimal monto_base_otros;

    @Column(name="monto_trib_vta_grat", precision=35, scale=20)
    private BigDecimal monto_trib_vta_grat;
    @Column(name="monto_otros_trib", precision=35, scale=20)
    private BigDecimal monto_otros_trib;
    @Column(name="monto_ivap", precision=35, scale=20)
    private BigDecimal monto_ivap;


    @Column(name="monto_icbper", precision=35, scale=20)
    private BigDecimal monto_icbper;

    @Column(name="monto_base_icbper", precision=35, scale=20)
    private BigDecimal monto_base_icbper;

    //hace referencia al tributo 1000
    @Column(name="afectacion_igv", precision=35, scale=20)
    private BigDecimal afectacion_igv;
    //hace referencia al tributo 2000
    @Column(name="sistema_isc", precision=35, scale=20)
    private BigDecimal sistema_isc;

    @Column(name="porcent_igv", precision=8, scale=3)
    private BigDecimal porcent_igv;
    @Column(name="porcent_ivap", precision=8, scale=3)
    private BigDecimal porcent_ivap;
    @Column(name="porcent_isc", precision=8, scale=3)
    private BigDecimal porcent_isc;
    @Column(name="porcent_otr_trib", precision=8, scale=3)
    private BigDecimal porcent_otr_trib;
    @Column(name="porcent_vta_grat", precision=8, scale=3)
    private BigDecimal porcent_vta_grat;

    @Column(name="cod_tip_sistema_isc", length=19)
    private String cod_tip_sistema_isc;
    @Column(name="cod_tip_afectacion_igv", length=19)
    private String cod_tip_afectacion_igv;

    @Column(name="valor_venta", precision=35, scale=20)
    private BigDecimal valor_venta;
    @Column(name="descuento", precision=35, scale=20)
    private BigDecimal descuento;
    @Column(name="codigo_dscto", length=3)
    private String codigo_dscto;

    @Column(name="estado", length=1)
    private String estado;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_payment_voucher")
    private ComprobantesEntity id_payment_voucher;


    //DETRACCION 027
    @Column
    private String detalleViajeDetraccion;
    @Column
    private String ubigeoOrigenDetraccion;
    @Column
    private String direccionOrigenDetraccion;
    @Column
    private String ubigeoDestinoDetraccion;
    @Column
    private String direccionDestinoDetraccion;
    @Column
    private BigDecimal valorServicioTransporte;
    @Column
    private BigDecimal valorCargaEfectiva;
    @Column
    private BigDecimal valorCargaUtil;

    //CAMPOS HIDROBIOLOGICOS

    @Column(name = "hidro_matricula")
    private String hidro_matricula;

    @Column(name = "hidro_embarcacion")
    private String hidro_embarcacion;

    @Column(name = "hidro_descripcion_tipo")
    private String hidro_descripcion_tipo;

    @Column(name = "hidro_lugar_descarga")
    private String hidro_lugar_descarga;

    @Column(name = "hidro_fecha_descarga")
    private String hidro_fecha_descarga;

    @Column(name = "hidro_cantidad")
    private String hidro_cantidad;

    @Override
    public String toString() {

        StringBuilder atributos = new StringBuilder();
        atributos.append("[DetailsPaymentVoucherEntity]").
                append(", idDetailsPayment: ").append(id_details_payment).
                append(", numeroItem: ").append(numero_item).
                append(", cantidad: ").append(cantidad).
                append(", codigoUnidadMedida: ").append(cod_unid_medida).
                append(", descripcion: ").append(descripcion_producto).
                append(", codigoProductoSunat: ").append(cod_prod_sunat).
                append(", codigoProducto: ").append(cod_producto).
                append(", codigoProductoGS1: ").append(cod_producto_gs1).
                append(", valorUnitario: ").append(valor_unit).
                append(", precioVentaUnitario: ").append(precio_venta_unit).
                append(", valorReferencialUnitario: ").append(valor_referencial_unit).
                append(", montoBaseIgv: ").append(monto_base_igv).
                append(", montoBaseIvap: ").append(monto_base_ivap).
                append(", montoBaseExportacion: ").append(monto_base_export).
                append(", montoBaseExonerado: ").append(monto_base_exone).
                append(", montoBaseInafecto: ").append(monto_base_inafec).
                append(", montoBaseGratuito: ").append(monto_base_grat).
                append(", montoBaseIsc: ").append(monto_base_isc).
                append(", montoBaseOtrosTributos: ").append(monto_base_otros).
                append(", tributoVentaGratuita: ").append(monto_trib_vta_grat).
                append(", otrosTributos: ").append(monto_otros_trib).
                append(", ivap: ").append(monto_ivap).
                append(", montoIcbper: ").append(monto_icbper).
                append(", montoBaseIcbper: ").append(monto_base_icbper).
                append(", afectacionIGV: ").append(afectacion_igv).
                append(", sistemaISC: ").append(sistema_isc).
                append(", porcentajeIgv: ").append(porcent_igv).
                append(", porcentajeIvap: ").append(porcent_ivap).
                append(", porcentajeIsc: ").append(porcent_isc).
                append(", porcentajeOtrosTributos: ").append(porcent_otr_trib).
                append(", porcentajeTributoVentaGratuita: ").append(porcent_vta_grat).
                append(", codigoTipoSistemaISC: ").append(cod_tip_sistema_isc).
                append(", codigoTipoAfectacionIGV: ").append(cod_tip_afectacion_igv).
                append(", valorVenta: ").append(valor_venta).
                append(", descuento: ").append(descuento).
                append(", codigoDescuento: ").append(codigo_dscto).
                append(", estado: ").append(estado).
                append(", paymentVoucher: ").append(id_payment_voucher).
                append(", detalleViajeDetraccion: ").append(detalleViajeDetraccion).
                append(", ubigeoOrigenDetraccion: ").append(ubigeoOrigenDetraccion).
                append(", direccionOrigenDetraccion: ").append(direccionOrigenDetraccion).
                append(", ubigeoDestinoDetraccion: ").append(ubigeoDestinoDetraccion).
                append(", direccionDestinoDetraccion: ").append(direccionDestinoDetraccion).
                append(", valorServicioTransporte: ").append(valorServicioTransporte).
                append(", valorCargaEfectiva: ").append(valorCargaEfectiva).
                append(", valorCargaUtil: ").append(valorCargaUtil).
                append(", hidroMatricula: ").append(hidro_matricula).
                append(", hidroEmbarcacion: ").append(hidro_embarcacion).
                append(", hidroDescripcionTipo: ").append(hidro_descripcion_tipo).
                append(", hidroLugarDescarga: ").append(hidro_lugar_descarga).
                append(", hidroFechaDescarga: ").append(hidro_fecha_descarga).
                append(", hidroCantidad: ").append(hidro_cantidad);

        return atributos.toString();
    }
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getIdDetailsPayment() {
        return id_details_payment;
    }

    public void setIdDetailsPayment(Long idDetailsPayment) {
        this.id_details_payment = idDetailsPayment;
    }

    public Integer getNumeroItem() {
        return numero_item;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numero_item = numeroItem;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodigoUnidadMedida() {
        return cod_unid_medida;
    }

    public void setCodigoUnidadMedida(String codigoUnidadMedida) {
        this.cod_unid_medida = codigoUnidadMedida;
    }

    public String getDescripcion() {
        return descripcion_producto;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion_producto = descripcion;
    }

    public String getCodigoProductoSunat() {
        return cod_prod_sunat;
    }

    public void setCodigoProductoSunat(String codigoProductoSunat) {
        this.cod_prod_sunat = codigoProductoSunat;
    }

    public String getCodigoProducto() {
        return cod_producto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.cod_producto = codigoProducto;
    }

    public String getCodigoProductoGS1() {
        return cod_producto_gs1;
    }

    public void setCodigoProductoGS1(String codigoProductoGS1) {
        this.cod_producto_gs1 = codigoProductoGS1;
    }

    public BigDecimal getValorUnitario() {
        return valor_unit;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valor_unit = valorUnitario;
    }

    public BigDecimal getPrecioVentaUnitario() {
        return precio_venta_unit;
    }

    public void setPrecioVentaUnitario(BigDecimal precioVentaUnitario) {
        this.precio_venta_unit = precioVentaUnitario;
    }

    public BigDecimal getValorReferencialUnitario() {
        return valor_referencial_unit;
    }

    public void setValorReferencialUnitario(BigDecimal valorReferencialUnitario) {
        this.valor_referencial_unit = valorReferencialUnitario;
    }

    public BigDecimal getMontoBaseIgv() {
        return monto_base_igv;
    }

    public void setMontoBaseIgv(BigDecimal montoBaseIgv) {
        this.monto_base_igv = montoBaseIgv;
    }

    public BigDecimal getMontoBaseIvap() {
        return monto_base_ivap;
    }

    public void setMontoBaseIvap(BigDecimal montoBaseIvap) {
        this.monto_base_ivap = montoBaseIvap;
    }

    public BigDecimal getMontoBaseExportacion() {
        return monto_base_export;
    }

    public void setMontoBaseExportacion(BigDecimal montoBaseExportacion) {
        this.monto_base_export = montoBaseExportacion;
    }

    public BigDecimal getMontoBaseExonerado() {
        return monto_base_exone;
    }

    public void setMontoBaseExonerado(BigDecimal montoBaseExonerado) {
        this.monto_base_exone = montoBaseExonerado;
    }

    public BigDecimal getMontoBaseInafecto() {
        return monto_base_inafec;
    }

    public void setMontoBaseInafecto(BigDecimal montoBaseInafecto) {
        this.monto_base_inafec = montoBaseInafecto;
    }

    public BigDecimal getMontoBaseGratuito() {
        return monto_base_grat;
    }

    public void setMontoBaseGratuito(BigDecimal montoBaseGratuito) {
        this.monto_base_grat = montoBaseGratuito;
    }

    public BigDecimal getMontoBaseIsc() {
        return monto_base_isc;
    }

    public void setMontoBaseIsc(BigDecimal montoBaseIsc) {
        this.monto_base_isc = montoBaseIsc;
    }

    public BigDecimal getMontoBaseOtrosTributos() {
        return monto_base_otros;
    }

    public void setMontoBaseOtrosTributos(BigDecimal montoBaseOtrosTributos) {
        this.monto_base_otros = montoBaseOtrosTributos;
    }

    public BigDecimal getTributoVentaGratuita() {
        return monto_trib_vta_grat;
    }

    public void setTributoVentaGratuita(BigDecimal tributoVentaGratuita) {
        this.monto_trib_vta_grat = tributoVentaGratuita;
    }

    public BigDecimal getOtrosTributos() {
        return monto_otros_trib;
    }

    public void setOtrosTributos(BigDecimal otrosTributos) {
        this.monto_otros_trib = otrosTributos;
    }

    public BigDecimal getIvap() {
        return monto_ivap;
    }

    public void setIvap(BigDecimal ivap) {
        this.monto_ivap = ivap;
    }

    public BigDecimal getMontoIcbper() {
        return monto_icbper;
    }

    public void setMontoIcbper(BigDecimal montoIcbper) {
        this.monto_icbper = montoIcbper;
    }

    public BigDecimal getMontoBaseIcbper() {
        return monto_base_icbper;
    }

    public void setMontoBaseIcbper(BigDecimal montoBaseIcbper) {
        this.monto_base_icbper = montoBaseIcbper;
    }

    public BigDecimal getAfectacionIGV() {
        return afectacion_igv;
    }

    public void setAfectacionIGV(BigDecimal afectacionIGV) {
        this.afectacion_igv = afectacionIGV;
    }

    public BigDecimal getSistemaISC() {
        return sistema_isc;
    }

    public void setSistemaISC(BigDecimal sistemaISC) {
        this.sistema_isc = sistemaISC;
    }

    public BigDecimal getPorcentajeIgv() {
        return porcent_igv;
    }

    public void setPorcentajeIgv(BigDecimal porcentajeIgv) {
        this.porcent_igv = porcentajeIgv;
    }

    public BigDecimal getPorcentajeIvap() {
        return porcent_ivap;
    }

    public void setPorcentajeIvap(BigDecimal porcentajeIvap) {
        this.porcent_ivap = porcentajeIvap;
    }

    public BigDecimal getPorcentajeIsc() {
        return porcent_isc;
    }

    public void setPorcentajeIsc(BigDecimal porcentajeIsc) {
        this.porcent_isc = porcentajeIsc;
    }

    public BigDecimal getPorcentajeOtrosTributos() {
        return porcent_otr_trib;
    }

    public void setPorcentajeOtrosTributos(BigDecimal porcentajeOtrosTributos) {
        this.porcent_otr_trib = porcentajeOtrosTributos;
    }

    public BigDecimal getPorcentajeTributoVentaGratuita() {
        return porcent_vta_grat;
    }

    public void setPorcentajeTributoVentaGratuita(BigDecimal porcentajeTributoVentaGratuita) {
        this.porcent_vta_grat = porcentajeTributoVentaGratuita;
    }

    public String getCodigoTipoSistemaISC() {
        return cod_tip_sistema_isc;
    }

    public void setCodigoTipoSistemaISC(String codigoTipoSistemaISC) {
        this.cod_tip_sistema_isc = codigoTipoSistemaISC;
    }

    public String getCodigoTipoAfectacionIGV() {
        return cod_tip_afectacion_igv;
    }

    public void setCodigoTipoAfectacionIGV(String codigoTipoAfectacionIGV) {
        this.cod_tip_afectacion_igv = codigoTipoAfectacionIGV;
    }

    public BigDecimal getValorVenta() {
        return valor_venta;
    }

    public void setValorVenta(BigDecimal valorVenta) {
        this.valor_venta = valorVenta;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public String getCodigoDescuento() {
        return codigo_dscto;
    }

    public void setCodigoDescuento(String codigoDescuento) {
        this.codigo_dscto = codigoDescuento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ComprobantesEntity getPaymentVoucher() {
        return id_payment_voucher;
    }

    public void setPaymentVoucher(ComprobantesEntity paymentVoucher) {
        this.id_payment_voucher = paymentVoucher;
    }

    public String getDetalleViajeDetraccion() {
        return detalleViajeDetraccion;
    }

    public void setDetalleViajeDetraccion(String detalleViajeDetraccion) {
        this.detalleViajeDetraccion = detalleViajeDetraccion;
    }

    public String getUbigeoOrigenDetraccion() {
        return ubigeoOrigenDetraccion;
    }

    public void setUbigeoOrigenDetraccion(String ubigeoOrigenDetraccion) {
        this.ubigeoOrigenDetraccion = ubigeoOrigenDetraccion;
    }

    public String getDireccionOrigenDetraccion() {
        return direccionOrigenDetraccion;
    }

    public void setDireccionOrigenDetraccion(String direccionOrigenDetraccion) {
        this.direccionOrigenDetraccion = direccionOrigenDetraccion;
    }

    public String getUbigeoDestinoDetraccion() {
        return ubigeoDestinoDetraccion;
    }

    public void setUbigeoDestinoDetraccion(String ubigeoDestinoDetraccion) {
        this.ubigeoDestinoDetraccion = ubigeoDestinoDetraccion;
    }

    public String getDireccionDestinoDetraccion() {
        return direccionDestinoDetraccion;
    }

    public void setDireccionDestinoDetraccion(String direccionDestinoDetraccion) {
        this.direccionDestinoDetraccion = direccionDestinoDetraccion;
    }

    public BigDecimal getValorServicioTransporte() {
        return valorServicioTransporte;
    }

    public void setValorServicioTransporte(BigDecimal valorServicioTransporte) {
        this.valorServicioTransporte = valorServicioTransporte;
    }

    public BigDecimal getValorCargaEfectiva() {
        return valorCargaEfectiva;
    }

    public void setValorCargaEfectiva(BigDecimal valorCargaEfectiva) {
        this.valorCargaEfectiva = valorCargaEfectiva;
    }

    public BigDecimal getValorCargaUtil() {
        return valorCargaUtil;
    }

    public void setValorCargaUtil(BigDecimal valorCargaUtil) {
        this.valorCargaUtil = valorCargaUtil;
    }

    public String getHidroMatricula() {
        return hidro_matricula;
    }

    public void setHidroMatricula(String hidroMatricula) {
        this.hidro_matricula = hidroMatricula;
    }

    public String getHidroEmbarcacion() {
        return hidro_embarcacion;
    }

    public void setHidroEmbarcacion(String hidroEmbarcacion) {
        this.hidro_embarcacion = hidroEmbarcacion;
    }

    public String getHidroDescripcionTipo() {
        return hidro_descripcion_tipo;
    }

    public void setHidroDescripcionTipo(String hidroDescripcionTipo) {
        this.hidro_descripcion_tipo = hidroDescripcionTipo;
    }

    public String getHidroLugarDescarga() {
        return hidro_lugar_descarga;
    }

    public void setHidroLugarDescarga(String hidroLugarDescarga) {
        this.hidro_lugar_descarga = hidroLugarDescarga;
    }

    public String getHidroFechaDescarga() {
        return hidro_fecha_descarga;
    }

    public void setHidroFechaDescarga(String hidroFechaDescarga) {
        this.hidro_fecha_descarga = hidroFechaDescarga;
    }

    public String getHidroCantidad() {
        return hidro_cantidad;
    }

    public void setHidroCantidad(String hidroCantidad) {
        this.hidro_cantidad = hidroCantidad;
    }
}