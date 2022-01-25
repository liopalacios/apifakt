package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * The persistent class for the company database table.
 *
 */
@Entity
@Table(name="clients")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="client_seq", sequenceName="client_seq", allocationSize=1 )
	@GeneratedValue(strategy= GenerationType.AUTO, generator="client_seq")
	private Integer id;

	@Column(name="tipo_documento")
	private String tipoDocumento;

	@Column(name="numero_documento")
	private String numeroDocumento;

	@Column(name="razon_social", length=500)
	private String razonSocial;

	@Column(name="nombre_comercial", length=500)
	private String nombreComercial;

	@Column(name="telefono_fijo", length=100)
	private String telefonoFijo;

	@Column(name="telefono_movil", length=100)
	private String telefonoMovil;

	@Column(name="direccion_fiscal", length=1000)
	private String direccionFiscal;

	@Column(name="email", length=100)
	private String email;
	
	@Column(name="condicion_pago", length=500)
	private String condicionPago;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne
	@JoinColumn(name="cod_company")
	private CompanyEntity company;

	@Column
	private boolean estado;

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