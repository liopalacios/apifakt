package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.GuiaRelacionada;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;

@Component
public class GuiaRelacionadaDeserializer extends FieldsInput<GuiaRelacionada> {

    @Override
    public GuiaRelacionada deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        GuiaRelacionada objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        String codigoTipoGuia = null;
        String serieNumeroGuia = null;


        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(codigoTipoGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "24[" + codigoTipoGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                codigoTipoGuia = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(serieNumeroGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "25[" + serieNumeroGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                serieNumeroGuia = campoTrama.textValue();
            }
        }

        objectResult = new GuiaRelacionada();
        objectResult.setCodigoTipoGuia(codigoTipoGuia);
        objectResult.setSerieNumeroGuia(serieNumeroGuia);


        return objectResult;
    }

}
