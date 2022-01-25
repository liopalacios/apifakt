package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="detail_guia_remision")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailGuiaRemisionEntity {

	@Id
	@SequenceGenerator(name="detail_guia_remision_seq", sequenceName = "detail_guia_remision_seq", allocationSize=1 )
	@GeneratedValue(strategy= GenerationType.AUTO, generator="detail_guia_remision_seq")
	@Column(name="id_detail_guia_remision")
	private Long idDetailGuiaRemision;
	@Column(name="numero_orden", nullable=false)
	private Integer numeroOrden;
	@Column(name="cantidad", nullable=false, precision=22, scale=10)
	private BigDecimal cantidad;
	@Column(name="unid_medida", nullable=false)
	private String unidadMedida;
	@Column(name="descripcion", length=250, nullable=false)
	private String descripcion;
	@Column(name="cod_item", length=16)
	private String codigoItem;

	@Column(name="estado", length=1)
	private String estado;

	@Column(name = "precio_unitario")
	private BigDecimal precioItem;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_guia_remision")
	private GuiaRemisionEntity guiaRemision;

	/*--------------------------------------------------------------------------------------------*/

	@Column(name="cod_unid_medida", length=3)
	private String codigoUnidadMedida;

	@Column(name="cod_prod_sunat", length=20)
	private String codigoProductoSunat;

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
	@Column(name="descuento", precision=35, scale=20)
	private BigDecimal descuento;
	@Column(name="codigo_dscto", length=3)
	private String codigoDescuento;
	@Column(name="peso")
	private BigDecimal peso;
	@Column(name="numero_serie")
	private String numero_serie;
	@Column(name="instruccion_especial")
	private String instruccion_especial;
}
