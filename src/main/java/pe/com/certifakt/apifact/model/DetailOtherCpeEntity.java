package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="detail_otros_cpe")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailOtherCpeEntity {

	@Id
	@SequenceGenerator(name="detail_otros_cpe_seq", sequenceName = "detail_otros_cpe_seq", allocationSize=1 )
	@GeneratedValue(strategy= GenerationType.AUTO, generator="detail_otros_cpe_seq")
	@Column(name="id_detail_otros_cpe")
	private Long idDetailOtherCpe;

	@Column(name="tipo_doc_relac", length=2, nullable=false)
	private String tipoDocumentoRelacionado;
	@Column(name="serie_doc_relac", length=4, nullable=false)
	private String serieDocumentoRelacionado;
	@Column(name="numero_doc_relac", nullable=false)
	private Integer numeroDocumentoRelacionado;
	@Column(name="fec_emision_doc_relac", length=10, nullable=false)
	private String fechaEmisionDocumentoRelacionado;
	@Column(name="importe_total_doc_relac", precision=12, scale=2, nullable=false)
	private BigDecimal importeTotalDocumentoRelacionado;
	@Column(name="cod_moneda_doc_relac", length=3, nullable=false)
	private String monedaDocumentoRelacionado;

	@Column(name="fecha_pago_cobro", length=10, nullable=false)
	private String fechaPagoCobro;
	@Column(name="numero_pago_cobro", length=9, nullable=false)
	private String numeroPagoCobro;
	@Column(name="pago_sin_ret_cobro", precision=12, scale=2, nullable=false)
	private BigDecimal importePagoSinRetencionCobro;
	@Column(name="cod_moneda_pago_cobro", length=3, nullable=false)
	private String monedaPagoCobro;

	@Column(name="importe_retenido_percibido", precision=12, scale=2, nullable=false)
	private BigDecimal importeRetenidoPercibido;
	@Column(name="cod_moneda_retenido_percibido", length=3, nullable=false)
	private String monedaImporteRetenidoPercibido;
	@Column(name="fecha_retencion_percepcion", length=10, nullable=false)
	private String fechaRetencionPercepcion;
	@Column(name="total_pagar_cobrar", precision=12, scale=2, nullable=false)
	private BigDecimal importeTotalToPagarCobrar;
	@Column(name="cod_moneda_tot_pag_cob", length=3, nullable=false)
	private String monedaImporteTotalToPagarCobrar;

	@Column(name="cod_moneda_ref_tip_camb", length=3)
	private String monedaReferenciaTipoCambio;
	@Column(name="cod_moneda_obj_tasa_camb", length=3)
	private String monedaObjetivoTasaCambio;
	@Column(name="tipo_cambio", precision=10, scale=6)
	private BigDecimal tipoCambio;
	@Column(name="fecha_cambio", length=10)
	private String fechaCambio;

	@Column(name="estado", length=1)
	private String estado;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_otros_cpe")
	private OtherCpeEntity otherCpe;

}
