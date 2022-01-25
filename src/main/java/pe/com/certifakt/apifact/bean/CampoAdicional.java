package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.model.AditionalFieldEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampoAdicional {

    private String nombreCampo;
    private String valorCampo;

    public static List<CampoAdicional> transformToBeanList(List<AditionalFieldEntity> adds) {
        List<CampoAdicional> resp = new ArrayList<>();
        if (adds == null) return resp;

        adds.forEach(aditionalFieldEntity -> {
            resp.add(CampoAdicional.builder()
                    .nombreCampo(aditionalFieldEntity.getTypeField().getName())
                    .valorCampo(aditionalFieldEntity.getValorCampo())
                    .build());
        });

        return resp;
    }


}
