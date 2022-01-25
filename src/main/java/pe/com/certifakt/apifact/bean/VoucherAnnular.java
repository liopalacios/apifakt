package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.deserializer.VoucherAnnularDeserializer;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = VoucherAnnularDeserializer.class)
public class VoucherAnnular implements Serializable {

    private String tipoComprobante;
    private String serie;
    private Integer numero;
    private String rucEmisor;
    private String fechaEmision;
    private String tipoComprobanteRelacionado;
    private String motivoAnulacion;

}
