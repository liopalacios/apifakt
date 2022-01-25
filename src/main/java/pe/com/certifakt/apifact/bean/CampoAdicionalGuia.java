package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.model.AditionalFieldEntity;
import pe.com.certifakt.apifact.model.AditionalFieldGuiaEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampoAdicionalGuia {

    private String nombreCampo;
    private String valorCampo;

    public static List<CampoAdicionalGuia> transformToBeanList(List<AditionalFieldGuiaEntity> adds) {
        List<CampoAdicionalGuia> resp = new ArrayList<>();
        if (adds == null) return resp;

        adds.forEach(aditionalFieldGuiaEntity -> {
            resp.add(CampoAdicionalGuia.builder()
                    .nombreCampo(aditionalFieldGuiaEntity.getTypeField().getName())
                    .valorCampo(aditionalFieldGuiaEntity.getValorCampo())
                    .build());
        });

        return resp;
    }
}
