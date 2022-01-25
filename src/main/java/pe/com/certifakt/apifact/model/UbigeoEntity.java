package pe.com.certifakt.apifact.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * The persistent class for the ubigeo database table.
 *
 */
@Entity
@Table(name = "ubigeo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UbigeoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_ubigeo", length=6)
	private String codigoUbigeo;

	@Column(name="departamento", length=120)
	private String departamento;

	@Column(name="provincia", length=120)
	private String provincia;

	@Column(name="distrito", length=120)
	private String distrito;

	@Column(name="estado", length=1)
	private String estado;

	@Column(name="descripcion")
	private String descripcion;


}