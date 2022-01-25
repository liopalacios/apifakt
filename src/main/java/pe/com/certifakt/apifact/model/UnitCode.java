package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "unit_code")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UnitCode {

    @Id
    @SequenceGenerator(name = "unit_code_seq", sequenceName = "unit_code_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "unit_code_seq")
    private Long id;

    @Column
    private String code;

    @Column
    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "unitCodes")
    private List<CompanyEntity> companyEntities = new ArrayList<>();


    @Column
    private Boolean esUsada;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitCode unitCode = (UnitCode) o;
        return Objects.equals(id, unitCode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
