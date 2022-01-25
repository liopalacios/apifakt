package pe.com.certifakt.apifact.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the detail_docs_summary database table.
 *
 */
@Entity
@Table(name="detail_docs_summary")
@Getter
@Setter
//@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailDocsSummaryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="detail_summary_seq", sequenceName="detail_summary_seq", allocationSize=1)
	@GeneratedValue(strategy= GenerationType.AUTO, generator="detail_summary_seq")
	@Column(name="id_detail_docs_summary")
	private Long idDetailDocsSummary;

	@Column(name="numero_item", nullable=false)
	private Integer numeroItem;
	@Column(name="serie_documento", length=4, nullable=false)
	private String serieDocumento;
	@Column(name="numero_documento", nullable=false)
	private Integer numeroDocumento;
	@Column(name="tipo_comprobante", length=2, nullable=false)
	private String tipoComprobante;

	@Column(name="tip_doc_ident_receptor", length=1)
	private String tipoDocIdentReceptor;
	@Column(name="num_doc_receptor", length=20)
	private String numDocReceptor;

	@Column(name="serie_afectado", length=4)
	private String serieAfectado;
	@Column(name="numero_afectado")
	private Integer numeroAfectado;
	@Column(name="tip_comprob_afectado", length=2)
	private String tipoComprobanteAfectado;

	@Column(name="estado_item", nullable=false)
	private Integer estadoItem;

	@Column(name="importe_total_venta", precision=12, scale=2)
	private BigDecimal importeTotalVenta;
	@Column(name="sumatoria_otro_cargos", precision=12, scale=2)
	private BigDecimal sumatoriaOtrosCargos;

	@Column(name="total_oper_exonerado", precision=12, scale=2)
	private BigDecimal totalValorVentaOperacionExonerado;
	@Column(name="total_oper_exportacion", precision=12, scale=2)
	private BigDecimal totalValorVentaOperacionExportacion;
	@Column(name="total_oper_gratuita", precision=12, scale=2)
	private BigDecimal totalValorVentaOperacionGratuita;
	@Column(name="total_oper_gravada", precision=12, scale=2)
	private BigDecimal totalValorVentaOperacionGravada;
	@Column(name="total_oper_inafecta", precision=12, scale=2)
	private BigDecimal totalValorVentaOperacionInafecta;

	@Column(name="total_igv", precision=12, scale=2)
	private BigDecimal totalIGV;
	@Column(name="total_isc", precision=12, scale=2)
	private BigDecimal totalISC;
	@Column(name="total_otros_trib", precision=12, scale=2)
	private BigDecimal totalOtrosTributos;

	@Column(name="estado", length=1)
	private String estado;

//	@ManyToOne(fetch = FetchType.LAZY)
	@ManyToOne
	@JoinColumn(name="id_docs_summary")
	private SummaryDocumentEntity summaryDocument;

}