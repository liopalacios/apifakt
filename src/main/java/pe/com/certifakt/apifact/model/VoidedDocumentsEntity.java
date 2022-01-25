package pe.com.certifakt.apifact.model;

import lombok.*;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * The persistent class for the documents_summary database table.
 */
@Entity
@Table(name = "voided_documents")
@Getter
@Setter
//@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoidedDocumentsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "documents_voided_seqpk", sequenceName = "documents_voided_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "documents_voided_seq")
    @Column(name = "id_document_voided")
    private Long idDocumentVoided;

    @Column(name = "correl_generac_dia", nullable = false)
    private Integer correlativoGeneracionDia;

    @Column(name = "estado", length = 10)
    private String estado;

    @Column(name = "fecha_baja_docs", length = 20)
    private String fechaBajaDocs;

    @Column(name = "fecha_generacion_baja", length = 20)
    private String fechaGeneracionBaja;

    @Column(name = "id_document", length = 17, nullable = false)
    private String idDocument;

    @Column(name = "ruc_emisor", length = 20, nullable = false)
    private String rucEmisor;

    @Column(name = "ticket_sunat", length = 50)
    private String ticketSunat;

    @Column(name = "code_response", length = 20)
    private String codigoRespuesta;

    @Column(name = "description_response", length = 3000)
    private String descripcionRespuesta;

    @Column(name = "fecha_generacion_resumen", nullable = false)
    private Timestamp fechaGeneracionResumen;

    @Column(name = "fecha_modificacion")
    private Timestamp fechaModificacion;

    @Column(name = "user_name", length = 150)
    private String userName;

    @Column(name = "user_name_modify", length = 150)
    private String userNameModify;

	@Column(name="estado_documento", length=2)
	private String estadoComprobante;

    @OneToMany(mappedBy = "voidedDocument", cascade = CascadeType.ALL)
    private List<DetailDocsVoidedEntity> detailBajaDocumentos;


    @OneToMany(mappedBy = "voidedDocument", cascade = CascadeType.ALL)
    private List<VoidedFileEntity> voidedFiles;

    @Column
    private Integer intentosGetStatus;

    public void addFile(VoidedFileEntity file){
        if (this.voidedFiles==null) this.voidedFiles = new ArrayList<>();
        file.setOrden(this.voidedFiles.size()+1);
        this.voidedFiles.add(file);
        file.setVoidedDocument(this);
    }

    public RegisterFileUploadEntity getXmlActivo() {
        Optional<VoidedFileEntity> resp = this.getVoidedFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.XML) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterFileUpload();
        else return null;
    }

    public RegisterFileUploadEntity getCdrActivo() {
        Optional<VoidedFileEntity> resp = this.getVoidedFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.CDR) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterFileUpload();
        else return null;
    }


    public List<DetailDocsVoidedEntity> getBajaDocumentos() {
        if (this.detailBajaDocumentos == null) {
            this.detailBajaDocumentos = new ArrayList<DetailDocsVoidedEntity>();
        }
        return this.detailBajaDocumentos;
    }

    public DetailDocsVoidedEntity addDetailDocsVoided(DetailDocsVoidedEntity detailDocsVoided) {
        getBajaDocumentos().add(detailDocsVoided);
        detailDocsVoided.setVoidedDocument(this);

        return detailDocsVoided;
    }

    @Override
    public String toString() {
    	StringBuilder atributos = new StringBuilder();
    	atributos.append("[VoidedDocumentsEntity]").
	    	append(", idDocumentVoided: ").append(idDocumentVoided).
    		append(", correlativoGeneracionDia: ").append(correlativoGeneracionDia).
    		append(", estado: ").append(estado).
    		append(", fechaBajaDocs: ").append(fechaBajaDocs).
    		append(", fechaGeneracionBaja: ").append(fechaGeneracionBaja).
    		append(", idDocument: ").append(idDocument).
    		append(", rucEmisor: ").append(rucEmisor).
    		append(", ticketSunat: ").append(ticketSunat).
    		append(", codigoRespuesta: ").append(codigoRespuesta).
    		append(", descripcionRespuesta: ").append(descripcionRespuesta).
    		append(", fechaGeneracionResumen: ").append(fechaGeneracionResumen).
    		append(", fechaModificacion: ").append(fechaModificacion).
    		append(", estadoComprobante: ").append(estadoComprobante).
    		append(", userName: ").append(userName).
    		append(", userNameModify: ").append(userNameModify);

    	return atributos.toString();
    }
}