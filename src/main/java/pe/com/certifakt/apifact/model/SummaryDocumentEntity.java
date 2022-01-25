package pe.com.certifakt.apifact.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


/**
 * The persistent class for the summary_documents database table.
 *
 */
@Entity
@Table(name="summary_documents")
@Getter
@Setter
//@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDocumentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="documents_summary_seq", sequenceName="documents_summary_seq", allocationSize=1 )
	@GeneratedValue(strategy= GenerationType.AUTO, generator="documents_summary_seq")
	@Column(name="id_document_summary")
	private Long idDocumentSummary;

	@Column(name="correlativo_dia", nullable=false)
	private Integer correlativoDia;

	@Column(name="estado", length=2)
	private String estado;

	@Column(name="fecha_emision", length=10, nullable=false)
	private String fechaEmision;

	@Column(name="fecha_generacion", length=10, nullable=false)
	private String fechaGeneracion;

	@Column(name="fecha_generacion_resumen")
	private Timestamp fechaGeneracionResumen;

	@Column(name="fecha_modificacion")
	private Timestamp fechaModificacion;

	@Column(name="id_document", length=17)
	private String idDocument;

	@Column(name="ruc_emisor", length=11)
	private String rucEmisor;

	@Column(name="ticket_sunat", length=100)
	private String ticketSunat;

	@Column(name="code_response", length=4)
	private String codigoRespuesta;

	@Column(name="description_response", length=2000)
	private String descripcionRespuesta;

	@Column(name="user_name", length=80)
	private String userName;

	@Column(name="user_name_modify", length=80)
	private String userNameModify;

	@Column(name="estado_documento", length=2)
	private String estadoComprobante;


	@Column
	private Integer intentosGetStatus;



	//bi-directional many-to-one association to DetailDocsSummary
	@OneToMany(mappedBy="summaryDocument", cascade = CascadeType.ALL)
	private List<DetailDocsSummaryEntity> detailDocsSummaries;


    @OneToMany(mappedBy="summaryDocument", cascade = CascadeType.ALL)
    private List<SummaryFileEntity> summaryFiles;

    public void addFile(SummaryFileEntity file){
        if (this.summaryFiles==null) this.summaryFiles = new ArrayList<>();
        file.setOrden(this.summaryFiles.size()+1);
        this.summaryFiles.add(file);
        file.setSummaryDocument(this);
    }


	public List<DetailDocsSummaryEntity> getDetailDocsSummaries() {
		if(this.detailDocsSummaries == null) {
			this.detailDocsSummaries = new ArrayList<DetailDocsSummaryEntity>();
		}
		return this.detailDocsSummaries;
	}

	public void setDetailDocsSummaries(List<DetailDocsSummaryEntity> detailDocsSummaries) {
		this.detailDocsSummaries = detailDocsSummaries;
	}

	public DetailDocsSummaryEntity addDetailDocsSummary(DetailDocsSummaryEntity detailDocsSummary) {
		getDetailDocsSummaries().add(detailDocsSummary);
		detailDocsSummary.setSummaryDocument(this);

		return detailDocsSummary;
	}

}