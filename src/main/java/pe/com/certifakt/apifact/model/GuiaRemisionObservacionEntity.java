package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="guia_remision_obs")
@Getter
@Setter
public class GuiaRemisionObservacionEntity {

	@Id
	@SequenceGenerator(name="guia_remision_obs_seq", sequenceName = "guia_remision_obs_seq", allocationSize=1 )
	@GeneratedValue(strategy= GenerationType.AUTO, generator="guia_remision_obs_seq")
	@Column(name="id_guia_remision_obs")
	private Long idGuiaRemisionObservacion;
	
	@Column(name="observacion_guia", length=250)
	private String observacion;
	
	@Column(name="estado", length=1)
	private String estado;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_guia_remision")
	private GuiaRemisionEntity guiaRemision;
	
}
