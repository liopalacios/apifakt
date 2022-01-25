package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.Anticipo;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class AnticipoDeserializer extends FieldsInput<Anticipo> {

    @Override
    public Anticipo deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        Anticipo objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        String serieAnticipo = null;
        Integer numeroAnticipo = null;
        String tipoDocumentoAnticipo = null;
        BigDecimal montoAnticipado = null;

        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(serieAnticipoLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "3[" + serieAnticipoLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                serieAnticipo = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(numeroAnticipoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numeroAnticipo = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + numeroAnticipoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(tipoDocumentoAnticipoLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "4[" + tipoDocumentoAnticipoLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoDocumentoAnticipo = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(montoAnticipadoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoAnticipado = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoAnticipadoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        objectResult = new Anticipo();
        objectResult.setMontoAnticipado(montoAnticipado);
        objectResult.setNumeroAnticipo(numeroAnticipo);
        objectResult.setSerieAnticipo(serieAnticipo);
        objectResult.setTipoDocumentoAnticipo(tipoDocumentoAnticipo);

        return objectResult;
    }

}
