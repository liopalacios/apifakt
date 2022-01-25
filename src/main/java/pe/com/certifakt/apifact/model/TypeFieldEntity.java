package pe.com.certifakt.apifact.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "type_field")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeFieldEntity {
	
	@Id
    @SequenceGenerator(name = "type_field_payment_voucher_seq", sequenceName = "type_field_payment_voucher_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "type_field_payment_voucher_seq")
    private Long id;
	
	@Column(name = "name", length = 100)
    private String name;
/*
	@JsonIgnore
	@OneToMany(mappedBy = "typeField", cascade = CascadeType.ALL)
    private List<AditionalFieldEntity> AditionalFields = new ArrayList<>();*/

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToMany
	@JoinTable(
	  name="TYPE_COMPONY_FIELD")
	private List<CompanyEntity> companys;
	
}
