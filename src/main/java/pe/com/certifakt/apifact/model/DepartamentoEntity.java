package pe.com.certifakt.apifact.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "departamento")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DepartamentoEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="cod_departamento", length=2)
	private String codigoDepartamento;

	@Column(name="descripcion", length=120)
	private String descripcion;
	
	@Column(name="estado", length=1)
	private Boolean estado;
	
	@OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL)
    private List<ProvinciaEntity> provincia;

}
