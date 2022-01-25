package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import pe.com.certifakt.apifact.deserializer.ComprobanteDeserializer;

import java.io.Serializable;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = ComprobanteDeserializer.class)
public class Comprobante implements Serializable {

    private String tipoComprobante;
    @JsonIgnore
    private String rucEmisor;
    private String serie;
    private Integer numero;
    private String estado;
    private String fechaEmision;
    private String moneda;
}
