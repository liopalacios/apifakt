package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import pe.com.certifakt.apifact.bean.Comprobante;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;

public class ComprobanteDeserializer extends FieldsInput<Comprobante> {

    @Override
    public Comprobante deserialize(JsonParser jsonParser,
                                   DeserializationContext context) throws IOException, JsonProcessingException {

        Comprobante objectResult;
        String mensajeError;
        JsonNode campoTrama;
        JsonNode trama;

        String tipoComprobante = null;
        String serie = null;
        Integer numero = null;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(tipoComprobanteForBusquedaByIdDocumentosLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "10[" + tipoComprobanteForBusquedaByIdDocumentosLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoComprobante = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(serieForBusquedaByIdDocumentosLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "11[" + serieForBusquedaByIdDocumentosLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                serie = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroForBusquedaByIdDocumentosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numero = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER
                        + "[" + numeroForBusquedaByIdDocumentosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        objectResult = new Comprobante();
        objectResult.setTipoComprobante(tipoComprobante);
        objectResult.setSerie(serie);
        objectResult.setNumero(numero);

        return objectResult;
    }

}
