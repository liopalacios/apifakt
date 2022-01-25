package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name="register_download_upload", indexes= {@Index(name="download_upload_name_file_idx", columnList="nombre_archivo")})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDownloadUploadEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name="download_upload_seq", sequenceName ="download_upload_seq", allocationSize=1)
    @GeneratedValue(strategy= GenerationType.AUTO, generator="download_upload_seq")
    @Column(name="id_register_download_send")
    private Long idRegisterDownloadSend;

    @Column(name="extension", length=4)
    private String extension;

    @Column(name="fecha_upload")
    private Timestamp fechaUpload;

    @Column(name="bucket", length=150)
    private String bucket;

    @Column(name="nombre_archivo", length=150)
    private String nombreOriginal;

    @Column(name="nombre_generado", length=150)
    private String nombreGenerado;

    @Column(name="ruc_company", length=11)
    private String rucCompany;

    @Column(name="tipo_archivo", length=2)
    private String tipoArchivo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="cod_company")
    private CompanyEntity company;

    @Column(name="estado", length=1)
    private String estado;

    private Boolean isOld;

    @PrePersist
    private void prePersist(){
        this.estado = "A";
        this.isOld = false;
    }
}
