package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.CampoAdicional;
import pe.com.certifakt.apifact.bean.PaymentVoucherCuota;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class CuotaDeserializer extends FieldsInput<PaymentVoucherCuota> {


    @Override
    public PaymentVoucherCuota deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        PaymentVoucherCuota objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        Integer numero = null;
        BigDecimal monto = null;
        String fecha = null;

        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);
        System.out.println("TRAMA");
        System.out.println(trama);
        campoTrama = trama.get(numeroCuotaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isNumber()) {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "5[" + numeroCuotaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numero = campoTrama.intValue();
            }
        }

        campoTrama = trama.get(montoCuotaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isNumber()) {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "6[" + montoCuotaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                monto = campoTrama.decimalValue();
            }
        }
        campoTrama = trama.get(fechaCuotaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "6[" + fechaCuotaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                fecha = campoTrama.textValue();
            }
        }

        objectResult = new PaymentVoucherCuota();
        objectResult.setNumero(numero);
        objectResult.setMonto(monto);
        objectResult.setFecha(fecha);


        return objectResult;
    }
}
