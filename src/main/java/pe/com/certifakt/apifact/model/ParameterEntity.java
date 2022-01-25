package pe.com.certifakt.apifact.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PARAMETER")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PARAMETER")
    private Integer id;

    @Column(name = "NAME", unique = true)
    private String name;

    @Column(name = "VALUE", length = 3000)
    private String value;

    @Column(name = "STATUS")
    private Boolean status;

}
