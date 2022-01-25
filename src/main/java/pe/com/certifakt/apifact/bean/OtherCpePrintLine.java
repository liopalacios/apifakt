
package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtherCpePrintLine {

    private String documento;
    private String fecha;
    private String importeTotal;
    private String numeroPago;
    private String tasa;
    private String importeRetenido;
    private String importePagado;
    private String tipoDoc;
    private String serie;
    private String numero;

}
