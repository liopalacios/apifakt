package pe.com.certifakt.apifact.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "reniec")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReniecEntity implements Serializable {

    @Id
    private String dni;

    private String nombres;


}
