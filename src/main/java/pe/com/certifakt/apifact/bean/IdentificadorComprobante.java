package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentificadorComprobante  implements Serializable {

    private String tipo;
    private String serie;
    private Integer numero;

}
