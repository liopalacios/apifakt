package pe.com.certifakt.apifact.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Getter
@Setter
@Table(name="oses" )
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OsesEntity implements Serializable {
    @Id
    @Column(name="oses_id")
    @SequenceGenerator(name="oses_oses_id_seq", sequenceName="oses_oses_id_seq", allocationSize=1 )
    @GeneratedValue(strategy=GenerationType.AUTO, generator="oses_oses_id_seq")
    private Integer id;

    @Column(name="name_oses")
    private String nameose;

    @Column(name="url_facturas")
    private String urlfacturas;

    @Column(name="url_guias")
    private String urlguias;

    @Column(name="url_othercpe")
    private String urlothercpe;

    @Column(name="url_consultacdr")
    private String urlconsultacdr;

}
