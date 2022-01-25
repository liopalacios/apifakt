package pe.com.certifakt.apifact.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConsultaComprobante {


    private String ruc;
    private String tipo;
    private String serie;
    private Integer numero;
    private String fecha;
    private BigDecimal monto;

}
