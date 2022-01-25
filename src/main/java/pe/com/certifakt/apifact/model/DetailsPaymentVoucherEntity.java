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
public class DetailsPaymentVoucherEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="detail_payment_voucher_seq", sequenceName = "detail_payment_voucher_seq", allocationSize=1 )
	@GeneratedValue(strategy= GenerationType.AUTO, generator="detail_payment_voucher_seq")
	@Column(name="id_details_payment")
	private Long idDetailsPayment;

	@Column(name="numero_item", nullable=false)
	private Integer numeroItem;
	@Column(name="cantidad", precision=12, scale=2)
	private BigDecimal cantidad;
	@Column(name="cod_unid_medida", length=3)
	private String codigoUnidadMedida;

	@Column(name="descripcion_producto", length=500)
	private String descripcion;
	@Column(name="cod_prod_sunat", length=20)
	private String codigoProductoSunat;
	@Column(name="cod_producto", length=30)
	private String codigoProducto;
	@Column(name="cod_producto_gs1", length=20)
	private String codigoProductoGS1;

	@Column(name="valor_unit", precision=35, scale=20)
	private BigDecimal valorUnitario;

	@Column(name="precio_venta_unit", precision=35, scale=20)
	private BigDecimal precioVentaUnitario;
	@Column(name="valor_referencial_unit", precision=35, scale=20)
	private BigDecimal valorReferencialUnitario;

	@Column(name="monto_base_igv", precision=35, scale=20)
	private BigDecimal montoBaseIgv;
	@Column(name="monto_base_ivap", precision=35, scale=20)
	private BigDecimal montoBaseIvap;
	@Column(name="monto_base_export", precision=35, scale=20)
	private BigDecimal montoBaseExportacion;
	@Column(name="monto_base_exone", precision=35, scale=20)
	private BigDecimal montoBaseExonerado;
	@Column(name="monto_base_inafec", precision=35, scale=20)
	private BigDecimal montoBaseInafecto;
	@Column(name="monto_base_grat", precision=35, scale=20)
	private BigDecimal montoBaseGratuito;
	@Column(name="monto_base_isc", precision=35, scale=20)
	private BigDecimal montoBaseIsc;
	@Column(name="monto_base_otros", precision=35, scale=20)
	private BigDecimal montoBaseOtrosTributos;

	@Column(name="monto_trib_vta_grat", precision=35, scale=20)
	private BigDecimal tributoVentaGratuita;
	@Column(name="monto_otros_trib", precision=35, scale=20)
	private BigDecimal otrosTributos;
	@Column(name="monto_ivap", precision=35, scale=20)
	private BigDecimal ivap;


	@Column(name="monto_icbper", precision=35, scale=20)
	private BigDecimal montoIcbper;

	@Column(name="monto_base_icbper", precision=35, scale=20)
	private BigDecimal montoBaseIcbper;

	//hace referencia al tributo 1000
	@Column(name="afectacion_igv", precision=35, scale=20)
	private BigDecimal afectacionIGV;
	//hace referencia al tributo 2000
	@Column(name="sistema_isc", precision=35, scale=20)
	private BigDecimal sistemaISC;

	@Column(name="porcent_igv", precision=8, scale=3)
	private BigDecimal porcentajeIgv;
	@Column(name="porcent_ivap", precision=8, scale=3)
	private BigDecimal porcentajeIvap;
	@Column(name="porcent_isc", precision=8, scale=3)
	private BigDecimal porcentajeIsc;
	@Column(name="porcent_otr_trib", precision=8, scale=3)
	private BigDecimal porcentajeOtrosTributos;
	@Column(name="porcent_vta_grat", precision=8, scale=3)
	private BigDecimal porcentajeTributoVentaGratuita;

	@Column(name="cod_tip_sistema_isc", length=19)
	private String codigoTipoSistemaISC;
	@Column(name="cod_tip_afectacion_igv", length=19)
	private String codigoTipoAfectacionIGV;

	@Column(name="valor_venta", precision=35, scale=20)
	private BigDecimal valorVenta;
	@Column(name="descuento", precision=35, scale=20)
	private BigDecimal descuento;
	@Column(name="codigo_dscto", length=3)
	private String codigoDescuento;

	@Column(name="estado", length=1)
	private String estado;


	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_payment_voucher")
	private PaymentVoucherEntity paymentVoucher;

	public void setPaymentVoucher(PaymentVoucherEntity paymentVoucher) {
		this.paymentVoucher = paymentVoucher;
	}

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
	private String hidroMatricula;

	@Column(name = "hidro_embarcacion")
	private String hidroEmbarcacion;

	@Column(name = "hidro_descripcion_tipo")
	private String hidroDescripcionTipo;

	@Column(name = "hidro_lugar_descarga")
	private String hidroLugarDescarga;

	@Column(name = "hidro_fecha_descarga")
	private String hidroFechaDescarga;

	@Column(name = "hidro_cantidad")
	private String hidroCantidad;

	@Column(name = "unidad_manejo")
	private String unidadManejo;

	@Column(name = "instrucciones_especiales")
	private String instruccionesEspeciales;

	@Column(name = "marca")
	private String marca;


}