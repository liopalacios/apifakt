package pe.com.certifakt.apifact.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.deserializer.PaymentVoucherParamsInputDeserializer;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = PaymentVoucherParamsInputDeserializer.class)
public class PaymentVoucherParamsInput implements Serializable {

    private String tipoComprobante;
    private String serie;
    private Integer numero;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String fechaEmisionDesde;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String fechaEmisionHasta;
    private String rucEmisor;
    private String tipoDocumentoReceptor;
    private String numeroDocumentoReceptor;

}
