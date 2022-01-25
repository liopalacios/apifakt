package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentPaypalDto implements Serializable {
    private Long id;
    private String fecha;
    private String hora;
    private String cliente;
    private String clienteemail;
    private String clienteid;
    private String clientedireccion;
    private String clientepais;
    private String clientepaiscod;
    private String ciudad;
    private String calle;
    private String vendedor;
    private String receptoremail;
    private String receptorid;
    private String productname;
    private String productid;
    private int cantidad;
    private Double envio;
    private Double igv;
    private String moneda;
    private Double cuota;
    private Double bruto;
    private Double bruto1;
    private String transaccion;
    private String comprobante;
    private String verify;
    private String datajson;
    private boolean estado;
    private String mensaje;
}
