package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.util.ConstantesSunat;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Summary implements Serializable {

    private String fechaEmision;// ReferenceDate: Fecha de emisión de los documentos (yyyy-mm-dd)
    private Integer nroResumenDelDia;
    private List<SummaryDetail> items;

    private String rucEmisor;
    private String denominacionEmisor;
    private String nombreComercialEmisor;
    private String tipoDocumentoEmisor;

    private String estadoComprobante;
//    private Party emisor;

    public String getId() {//nombre del archivo sin la extencion y sin el ruc
        return new StringBuilder(ConstantesSunat.RESUMEN_DIARIO_BOLETAS).
                append("-").
                append(fechaEmision.replace("-", "")).
                append("-").append(nroResumenDelDia).toString();
    }

    public String getSignId() {//id de la firma
        return new StringBuilder("SRC-").
                append(fechaEmision.replace("-", "")).
                append("-").append(nroResumenDelDia).toString();
    }

    public String getUriExternalReference() {
        return new StringBuilder(rucEmisor).
                append("-RC-").
                append(fechaEmision.replace("-", "")).
                append("-").append(nroResumenDelDia).toString();
    }
}
