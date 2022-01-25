package pe.com.certifakt.apifact.bean;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanySummary {
    private Integer id;
    private String razonSocial;
    private String nombreComercial;
    private String representante;
    private String telefono;
    private String email;
    private String ruc;
    private String direccion;
    private Boolean esProduccion;
    private Boolean viewGuia;
    private Boolean viewOtroComprobante;
}
