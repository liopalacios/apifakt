package pe.com.certifakt.apifact.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GetStatusExcelDTO {

    private String filtroDesde;
    private String filtroHasta;
    private String filtroTipoComprobante;
    private Integer filtroSerie;
    private Long filtroEmail;
}
