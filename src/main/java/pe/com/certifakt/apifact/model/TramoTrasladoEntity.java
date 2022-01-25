package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="tramos_guia_remision")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TramoTrasladoEntity {

	@Id
	@SequenceGenerator(name="tramo_guia_remision_seq", sequenceName = "tramo_guia_remision_seq" , allocationSize=1)
	@GeneratedValue(strategy= GenerationType.AUTO, generator="tramo_guia_remision_seq")
	@Column(name="id_tramo_guia_remision")
	private Long idTramoGuiaRemision;
	@Column(name="corre_tramo", nullable=false)
	private Integer correlativoTramo;
	@Column(name="modalidad_traslado", length=2, nullable=false)
	private String modalidadTraslado;
	@Column(name="fecha_inic_traslado", length=10, nullable=false)
	private String fechaInicioTraslado;
	//Transportista (transporte publico)
	@Column(name="num_docum_ident_transp", length=15)
	private String numeroDocumentoIdentidadTransportista;
	@Column(name="tipo_docum_ident_transp", length=1)
	private String tipoDocumentoIdentidadTransportista;
	@Column(name="denominacion_transp", length=100)
	private String denominacionTransportista;
	//Vehiculo (transporte privado)
	@Column(name="numero_placa", length=8)
	private String numeroPlacaVehiculo;
	//Conductor transporte privado
	@Column(name="num_docum_ident_conduct", length=15)
	private String numeroDocumentoIdentidadConductor;
	@Column(name="tipo_docum_ident_conduct", length=1)
	private String tipoDocumentoIdentidadConductor;

	@Column(name="estado", length=1)
	private String estado;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_guia_remision")
	private GuiaRemisionEntity guiaRemision;
}
