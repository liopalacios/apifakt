package pe.com.certifakt.apifact.model;

import lombok.*;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "document_download_file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDownloadFileEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "file_descarga_documento_seq", sequenceName = "file_descarga_documento_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "file_descarga_documento_seq")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoArchivoEnum tipoArchivo;

    @Enumerated(EnumType.STRING)
    private EstadoArchivoEnum estadoArchivo;

    @ManyToOne
    @JoinColumn(name = "id_register_download_send")
    private RegisterDownloadUploadEntity registerDownloadUpload;


    @ManyToOne
    @JoinColumn(name = "id_download_excel")
    private DowloadExcelEntity downloadExcel;

    private Integer orden;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentDownloadFileEntity that = (DocumentDownloadFileEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
