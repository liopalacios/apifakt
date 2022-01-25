package pe.com.certifakt.apifact.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the detail_docs_summary database table.
 *
 */
@Entity
@Table(name="detail_docs_voided")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DetailDocsVoidedEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="detail_voided_seq", sequenceName="detail_voided_seq", allocationSize=1 )
	@GeneratedValue(strategy= GenerationType.AUTO, generator="detail_voided_seq")
	@Column(name="id_detail_docs_voided")
	private Long idDetailDocsSummary;

	@Column(name="numero_item")
	private Integer numeroItem;

	@Column(name="serie_documento", length=4, nullable=false)
	private String serieDocumento;
	@Column(name="numero_documento", nullable=false)
	private Integer numeroDocumento;
	@Column(name="tipo_comprobante", length=2, nullable=false)
	private String tipoComprobante;
	@Column(name="motivo_baja", length=100, nullable=false)
	private String motivoBaja;

	@Column(name="estado", length=1)
	private String estado;

//	@ManyToOne(fetch = FetchType.LAZY)
	@ManyToOne
	@JoinColumn(name="id_docs_voided")
	private VoidedDocumentsEntity voidedDocument;

}