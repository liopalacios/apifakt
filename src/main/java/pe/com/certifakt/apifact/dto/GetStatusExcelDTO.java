package pe.com.certifakt.apifact.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GetStatusExcelDTO implements Serializable {

    private String uuidSaved;
    private String tipoComprobante;
    private String serie;
    private Integer numero;
    private String nameDocument;
    private Long idDowloadExcel;


}
