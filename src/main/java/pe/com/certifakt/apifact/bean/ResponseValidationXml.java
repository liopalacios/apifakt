package pe.com.certifakt.apifact.bean;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseValidationXml  implements Serializable {

    private Boolean hasError;
    private Boolean hasWarning;
    private List<ErrorXml> errors;
    private String ruc;
    private String tipo;
    private String serie;
    private String numero;


}
