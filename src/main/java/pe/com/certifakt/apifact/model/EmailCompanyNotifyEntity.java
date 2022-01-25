package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "email_company_notify")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailCompanyNotifyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "email_company_seq", sequenceName = "email_company_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "email_company_seq")
    private Long id;

    private String email;

    @ManyToOne
    @JoinColumn(name = "cod_company")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CompanyEntity company;

    @Column
    private boolean estado;

    @PrePersist
    void onPrePersist() {
        this.estado = true;
    }

}