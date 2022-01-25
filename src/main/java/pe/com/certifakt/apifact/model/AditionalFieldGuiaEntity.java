package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "aditional_field_guia")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AditionalFieldGuiaEntity {

    @Id
    @SequenceGenerator(name = "aditional_field_guia_seq", sequenceName = "aditional_field_guia_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "aditional_field_guia_seq")
    private Long id;

    @Column
    private String nombreCampo;

    private String valorCampo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_guia_remision")
    @JsonIgnore
    private GuiaRemisionEntity guiaRemision;


    @ManyToOne(cascade = CascadeType.ALL)
    private TypeFieldEntity typeField;
}
