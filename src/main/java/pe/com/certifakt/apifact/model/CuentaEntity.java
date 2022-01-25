package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cuenta_bancaria")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CuentaEntity {

    @Id
    @SequenceGenerator(name = "cuenta_bancaria_seq", sequenceName = "cuenta_bancaria_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cuenta_bancaria_seq")
    private Long id;

    @Column
    private String banco;


    @Column
    private String name;


    @Column
    private String number;
    
    @Column
    private String cci;
    
    @Column
    private Boolean detraccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cod_company")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CompanyEntity company;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuentaEntity cuentaEntity = (CuentaEntity) o;
        return Objects.equals(id, cuentaEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
