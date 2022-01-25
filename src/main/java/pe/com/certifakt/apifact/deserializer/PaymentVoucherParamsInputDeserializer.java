package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import pe.com.certifakt.apifact.bean.PaymentVoucherParamsInput;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;

public class PaymentVoucherParamsInputDeserializer extends FieldsInput<PaymentVoucherParamsInput> {

    @Override
    public PaymentVoucherParamsInput deserialize(JsonParser jsonParser,
                                                 DeserializationContext context) throws IOException, JsonProcessingException {

        PaymentVoucherParamsInput objectResult;
        JsonNode campoTrama;
        JsonNode trama;

        String tipoComprobante = null;
        String serie = null;
        Integer numero = null;
        String fechaEmisionDesde;
        String fechaEmisionHasta;
        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(tipoComprobanteToBuscarLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "60[" + tipoComprobanteToBuscarLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoComprobante = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(serieToBuscarLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "62[" + serieToBuscarLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                serie = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroToBuscarLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numero = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + numeroToBuscarLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(fechaEmisionDesdeToBuscarLabel);
        fechaEmisionDesde = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(fechaEmisionHastaToBuscarLabel);
        fechaEmisionHasta = (campoTrama != null) ? campoTrama.textValue() : null;

        objectResult = new PaymentVoucherParamsInput();
        objectResult.setTipoComprobante(tipoComprobante);
        objectResult.setSerie(serie);
        objectResult.setNumero(numero);
        objectResult.setFechaEmisionDesde(fechaEmisionDesde);
        objectResult.setFechaEmisionHasta(fechaEmisionHasta);

        return objectResult;
    }

}
