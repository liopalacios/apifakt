package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import pe.com.certifakt.apifact.bean.VoucherAnnular;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;

public class VoucherAnnularDeserializer extends FieldsInput<VoucherAnnular> {

    @Override
    public VoucherAnnular deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {

        VoucherAnnular objectResult;
        JsonNode campoTrama;
        JsonNode trama;
        String mensajeError;

        String tipoComprobanteRelacionado = null;
        String tipoComprobante;
        String serieDocumento = null;
        String motivoAnulacion = null;
        Integer numeroDocumento = null;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(tipoComprobanteToAnularLabel);
        tipoComprobante = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(serieToAnularLabel);
        if (campoTrama != null) {
            if (campoTrama.isTextual()) {
                serieDocumento = campoTrama.textValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "71[" + serieToAnularLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(numeroToAnularLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numeroDocumento = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + numeroToAnularLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(motivoToAnularLabel);
        if (campoTrama != null) {
            if (campoTrama.isTextual()) {
                motivoAnulacion = campoTrama.textValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "72[" + motivoToAnularLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(tipoComprobanteRelacionadoToAnularLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isTextual()) {
                tipoComprobanteRelacionado = campoTrama.textValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "73[" + tipoComprobanteRelacionadoToAnularLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        objectResult = new VoucherAnnular();
        objectResult.setNumero(numeroDocumento);
        objectResult.setSerie(serieDocumento);
        objectResult.setTipoComprobante(tipoComprobante);
        objectResult.setTipoComprobanteRelacionado(tipoComprobanteRelacionado);
        objectResult.setMotivoAnulacion(motivoAnulacion);

        return objectResult;
    }

}
