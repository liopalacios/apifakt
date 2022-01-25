package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pe.com.certifakt.apifact.security.UserPrincipal;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USERS",indexes = { @Index(name = "USERNAME_INDICE", columnList = "DE_LOGIN") })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_USER")
    private Long id;

    @Column(name = "DE_LOGIN", length=80)
    private String username;

    @Column(name = "PASSWORD", length=150)
    private String password;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column
    private String dni;

    @Column(name = "type_user", length=2)
    private String typeUser;

    @Column(name = "ENABLED")
    private Boolean enabled;

    @Column(name = "LASTPASSWORDRESETDATE")
    private Date lastPasswordResetDate;

    @Column
    private Boolean estado;

    @Transient
    private String passwordTemp;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name="cod_company")
    private CompanyEntity company;

    @JsonIgnore
    @ManyToOne
    private BranchOfficeEntity oficina;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_AUTHORITY",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID_USER")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    private List<Authority> authorities;



}