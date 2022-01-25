package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.com.certifakt.apifact.config.UnixTimestampDateSerializer;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "dowload_excel")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DowloadExcelEntity implements Serializable {

    @Id
    @SequenceGenerator(name="dowload_excel_seq", sequenceName = "dowload_excel_seq" , allocationSize=1)
    @GeneratedValue(strategy= GenerationType.AUTO, generator="dowload_excel_seq")
    @Column(name = "id_dowload_excel")
    private Long idExcelDocument;

    @Column(name = "identificador")
    private String identificador;
    @Column(name = "tipo_documento")
    private String tipoDocumento;

    @Column(name = "serie")
    private String serie;
    @Column(name = "estado_descarga")
    private String estadoDescarga;
    @Column(name = "link_s3")
    private String linkS3;
    @Column(name = "fecha_solicitud")
    private String fechaSolicitud;
    @Column(name = "cod_company")
    private Integer codCompany;

    @JsonIgnore
    @OneToMany(mappedBy = "downloadExcel", cascade = CascadeType.ALL)
    private List<DocumentDownloadFileEntity> documentDownloadFile = new ArrayList<>();

    @JsonSerialize(using = UnixTimestampDateSerializer.class)
    @Column(name = "fecha_registro", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;

    public void addFile(DocumentDownloadFileEntity file) {
        file.setOrden(this.getFiles().size() + 1);

        if (file.getTipoArchivo().equals(TipoArchivoEnum.XLS)) {
            getFiles().forEach(f -> {
                if (f.getTipoArchivo().equals(TipoArchivoEnum.XLS)) {
                    f.setEstadoArchivo(EstadoArchivoEnum.INACTIVO);
                }
            });
        }
        getFiles().add(file);
        file.setDownloadExcel(this);
    }
    @JsonIgnore
    public List<DocumentDownloadFileEntity> getFiles() {
        if (this.documentDownloadFile == null) {
            this.documentDownloadFile = new ArrayList<>();
        }
        return this.documentDownloadFile;
    }
    @JsonIgnore
    public RegisterDownloadUploadEntity getExcelActivo() {
        Optional<DocumentDownloadFileEntity> resp = this.getFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.XLS) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterDownloadUpload();
        else return null;
    }

    @JsonIgnore
    public RegisterDownloadUploadEntity getXlsActivo() {
        Optional<DocumentDownloadFileEntity> resp = this.getFiles().stream().filter(f -> f.getTipoArchivo().equals(TipoArchivoEnum.XLS) && f.getEstadoArchivo().equals(EstadoArchivoEnum.ACTIVO)).findFirst();
        if (resp.isPresent())
            return resp.get().getRegisterDownloadUpload();
        else return null;
    }


}
