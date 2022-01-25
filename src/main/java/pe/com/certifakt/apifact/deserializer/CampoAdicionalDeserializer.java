package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.CampoAdicional;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;

@Component
public class CampoAdicionalDeserializer extends FieldsInput<CampoAdicional> {

    @Override
    public CampoAdicional deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        CampoAdicional objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        String nombreCampo = null;
        String valorCampo = null;

        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(nombreCampoAdicionalLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "5[" + nombreCampoAdicionalLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                nombreCampo = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(valorCampoAdicionalLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                //mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "6[" + valorCampoAdicionalLabel + "]";
                //throw new DeserializerException(mensajeError);
                valorCampo = "-";
            } else {
                valorCampo = campoTrama.textValue().length()>1800?
                        campoTrama.textValue().substring(0,1800):campoTrama.textValue();
            }
        }


        objectResult = new CampoAdicional();
        objectResult.setNombreCampo(nombreCampo);
        objectResult.setValorCampo(valorCampo);


        return objectResult;
    }

}
