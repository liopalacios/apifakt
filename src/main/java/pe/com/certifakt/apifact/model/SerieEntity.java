package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the company database table.
 *
 */
@Entity
@Table(name="series")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SerieEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="serie_seq", sequenceName="serie_seq" , allocationSize=1)
	@GeneratedValue(strategy= GenerationType.AUTO, generator="serie_seq")
	private Integer id;

	@Column(name="tipo_documento")
	private String tipoDocumento;

	@Column
	private String serie;

	@JsonIgnore
	@ManyToOne
	private BranchOfficeEntity oficina;

	@JsonIgnore
	@Column
	private Date createdOn;

	@JsonIgnore
	@Column
	private String createdBy;

	@JsonIgnore
	@Column
	private String updatedBy;

	@JsonIgnore
	@Column
	private Date updatedOn;

	@PrePersist
    void onPrePersist() {
		this.createdOn=new Date();
	}

	@PreUpdate
    void onPreUpdate() {
		this.updatedOn=new Date();
	}
}