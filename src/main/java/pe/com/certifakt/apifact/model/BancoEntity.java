package pe.com.certifakt.apifact.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "banco")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class BancoEntity {

    @Id
    @SequenceGenerator(name = "banco_seq", sequenceName = "banco_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "banco_seq")
    private Long id;

    @Column
    private String code;

    @Column
    private String name;


}
