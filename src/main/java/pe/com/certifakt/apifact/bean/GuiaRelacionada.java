package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.model.GuiaRelacionadaEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuiaRelacionada implements Serializable {

    private String codigoTipoGuia;
    private String serieNumeroGuia;

    public static List<GuiaRelacionada> transformToBeanList(List<GuiaRelacionadaEntity> ebgs) {
        List<GuiaRelacionada> response = new ArrayList<>();
        if (ebgs == null) return null;

        ebgs.forEach(guiaRelacionadaEntity -> {
            response.add(GuiaRelacionada.builder()
                    .codigoTipoGuia(guiaRelacionadaEntity.getCodigoTipoGuia())
                    .serieNumeroGuia(guiaRelacionadaEntity.getSerieNumeroGuia())
                    .build());
        });
        return response;
    }
}
