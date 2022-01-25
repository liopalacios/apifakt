package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.TramoTraslado;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class TramoTrasladoDeserializer extends FieldsInput<TramoTraslado> {
    String pattern = "yyyy-MM-dd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    @Override
    public TramoTraslado deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        Date date = new Date();
        // TODO Auto-generated method stub
        TramoTraslado tramoTraslado;
        JsonNode trama;
        JsonNode campoTrama;

        String modalidadTraslado = null;
        String fechaInicioTraslado = null;
        String numeroDocumentoIdentidadTransportista = null;
        String tipoDocumentoIdentidadTransportista = null;
        String denominacionTransportista = null;
        String numeroPlacaVehiculo = null;
        String numeroDocumentoIdentidadConductor = null;
        String tipoDocumentoIdentidadConductor = null;


        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(modalidadTrasladoGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "63[" + modalidadTrasladoGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                modalidadTraslado = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(fechaInicioTrasladoGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                //mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "64[" + fechaInicioTrasladoGuiaLabel + "]";
                fechaInicioTraslado = simpleDateFormat.format(date);
                //throw new DeserializerException(mensajeError);
            } else {
                fechaInicioTraslado = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroDocumentoIdentidadTransportistaGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "65[" + numeroDocumentoIdentidadTransportistaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numeroDocumentoIdentidadTransportista = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(tipoDocumentoIdentidadTransportistaGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "66[" + tipoDocumentoIdentidadTransportistaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoDocumentoIdentidadTransportista = campoTrama.textValue();
            }
        }


        campoTrama = trama.get(denominacionTransportistaGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "67[" + denominacionTransportistaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                denominacionTransportista = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroPlacaVehiculoGuiaLabel);
        if (!campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "68[" + numeroPlacaVehiculoGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numeroPlacaVehiculo = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroDocumentoIdentidadConductorGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "69[" + numeroDocumentoIdentidadConductorGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numeroDocumentoIdentidadConductor = campoTrama.textValue();
            }
        }


        campoTrama = trama.get(tipoDocumentoIdentidadConductorGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "70[" + tipoDocumentoIdentidadConductorGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoDocumentoIdentidadConductor = campoTrama.textValue();
            }
        }

        tramoTraslado = new TramoTraslado();
        tramoTraslado.setModalidadTraslado(modalidadTraslado);
        tramoTraslado.setFechaInicioTraslado(fechaInicioTraslado);
        tramoTraslado.setNumeroDocumentoIdentidadTransportista(numeroDocumentoIdentidadTransportista);
        tramoTraslado.setTipoDocumentoIdentidadTransportista(tipoDocumentoIdentidadTransportista);
        tramoTraslado.setDenominacionTransportista(denominacionTransportista);
        tramoTraslado.setNumeroPlacaVehiculo(numeroPlacaVehiculo);
        tramoTraslado.setNumeroDocumentoIdentidadConductor(numeroDocumentoIdentidadConductor);
        tramoTraslado.setTipoDocumentoIdentidadConductor(tipoDocumentoIdentidadConductor);

        return tramoTraslado;

    }

}
