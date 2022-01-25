package pe.com.certifakt.apifact.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="error_catalog")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorEntity {

	@Id
	@SequenceGenerator(name="error_catalog_seq", sequenceName = "error_catalog_seq" , allocationSize=1)
	@GeneratedValue(strategy= GenerationType.AUTO, generator="error_catalog_seq")
	private Integer id;

    private String code;

    private String description;

    private String type;

    private String document;

}
