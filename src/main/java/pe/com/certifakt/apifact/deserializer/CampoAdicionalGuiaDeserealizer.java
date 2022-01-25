package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.CampoAdicional;
import pe.com.certifakt.apifact.bean.CampoAdicionalGuia;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;

@Component
public class CampoAdicionalGuiaDeserealizer extends FieldsInput<CampoAdicionalGuia> {
    @Override
    public CampoAdicionalGuia deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        CampoAdicionalGuia objectResult;
        JsonNode trama;
        JsonNode campoTrama;
        String nombreCampo = null;
        String valorCampo = null;
        String mensajeError;
        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(nombreCampoAdicionalGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "7[" + nombreCampoAdicionalGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                nombreCampo = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(valorCampoAdicionalGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "8[" + valorCampoAdicionalGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                valorCampo = campoTrama.textValue().length()>600?
                        campoTrama.textValue().substring(0,600):campoTrama.textValue();

                valorCampo = valorCampo.replace("?", "");
            }
        }
        objectResult = new CampoAdicionalGuia();
        objectResult.setNombreCampo(nombreCampo);
        objectResult.setValorCampo(valorCampo);


        return objectResult;
    }
}
