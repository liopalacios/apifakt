package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the company database table.
 *
 */
@Entity
@Table(name="branch_offices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class,property="@id", scope = SerieEntity.class)
public class BranchOfficeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="branchoffice_seq", sequenceName="branchoffice_seq" , allocationSize=1)
	@GeneratedValue(strategy= GenerationType.AUTO, generator="branchoffice_seq")
	private Integer id;

	@Column(name="nombre_corto")
	private String nombreCorto;

	@Column
	private String departamento;

	@Column
	private String provincia;

	@Column
	private String distrito;

	@Column
	private String direccion;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne
	@JoinColumn(name="cod_company")
	private CompanyEntity company;


	@OneToMany(mappedBy="oficina", cascade = CascadeType.ALL)
	private List<SerieEntity> series=new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy="oficina", cascade = CascadeType.ALL)
	private List<User> usuarios=new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy="oficina", cascade = CascadeType.ALL)
	private List<PaymentVoucherEntity> paymentVoucher=new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy="oficina", cascade = CascadeType.ALL)
	private List<GuiaRemisionEntity> guiaRemision = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy="oficina", cascade = CascadeType.ALL)
	private List<OtherCpeEntity> otherCpe = new ArrayList<>();

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

	public void addUser(User user){
	    user.setOficina(this);
	    this.usuarios.add(user);
    }

    public void addSerie(SerieEntity serie){
        serie.setOficina(this);
        this.series.add(serie);
    }

    public void addSeries(List<SerieEntity> series){
	    series.forEach(serieEntity -> {serieEntity.setOficina(this);});
        this.series.addAll(series);
    }

	@PrePersist
    void onPrePersist() {
		this.createdOn=new Date();
	}

	@PreUpdate
    void onPreUpdate() {
		this.updatedOn=new Date();
	}
}