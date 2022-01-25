package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the register_file_upload database table.
 *
 */
@Entity
@Table(name="register_file_upload", indexes= {@Index(name="file_upload_name_file_idx", columnList="nombre_archivo")})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterFileUploadEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="file_upload_seq", sequenceName ="file_upload_seq", allocationSize=1)
	@GeneratedValue(strategy= GenerationType.AUTO, generator="file_upload_seq")
	@Column(name="id_register_file_send")
	private Long idRegisterFileSend;

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

	@Column(name="usuario_upload", length=80)
	private String usuarioUpload;

	@Column(name="uuid", length=200)
	private String uuid;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="cod_company")
	private CompanyEntity company;

	@Column(name="estado", length=1)
	private String estado;

	@Column(name="fecha_modificacion")
	private Timestamp fechaModificacion;

	@Column(name="user_name_modify", length=80)
	private String userNameModify;

	private Boolean isOld;

	@PrePersist
	private void prePersist(){
		this.estado = "A";
		this.isOld = false;
	}

}