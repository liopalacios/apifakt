package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "distrito")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DistritoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="cod_distrito", length=6)
	private String codigoDistrito;

	@Column(name="descripcion", length=120)
	private String descripcion;
	
	@Column(name="estado", length=1)
	private Boolean estado;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cod_provincia")
	private ProvinciaEntity provincia;
	
}

