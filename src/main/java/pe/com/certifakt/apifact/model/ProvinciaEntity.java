package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "provincia")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProvinciaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="cod_provincia", length=4)
	private String codigoProvincia;

	@Column(name="descripcion", length=120)
	private String descripcion;
	
	@Column(name="estado", length=1)
	private Boolean estado;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cod_departamento")
	private DepartamentoEntity departamento;
	
	@OneToMany(mappedBy = "provincia", cascade = CascadeType.ALL)
    private List<DistritoEntity> distrito;

}

