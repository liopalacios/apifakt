package pe.com.certifakt.apifact.model;

import lombok.*;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


/**
 * The persistent class for the payment_voucher database table.
 */
@Entity
@Table(name = "other_cpe_file")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtherCpeFileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "file_other_cpe_seq", sequenceName = "file_other_cpe_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "file_other_cpe_seq")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoArchivoEnum tipoArchivo;

    @Enumerated(EnumType.STRING)
    private EstadoArchivoEnum estadoArchivo;

    @ManyToOne
    @JoinColumn(name = "id_register_file_send")
    private RegisterFileUploadEntity registerFileUpload;

    @ManyToOne
    @JoinColumn(name = "id_otros_cpe")
    private OtherCpeEntity otherCpe;

    private Integer orden;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherCpeFileEntity that = (OtherCpeFileEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
