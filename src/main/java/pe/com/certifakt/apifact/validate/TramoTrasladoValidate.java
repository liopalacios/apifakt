package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.TramoTraslado;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.util.FieldsInput;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.io.IOException;

@Component
public class TramoTrasladoValidate extends FieldsInput<Object> {

    public void validateTramoValidate(TramoTraslado tramoTraslado) throws ValidatorFieldsException {

        validateModalidadTraslado(tramoTraslado.getModalidadTraslado());
        validateFechaInicioTraslado(tramoTraslado.getFechaInicioTraslado());
        validateConductorTransportePublico(tramoTraslado.getNumeroDocumentoIdentidadTransportista(),
                tramoTraslado.getTipoDocumentoIdentidadTransportista(),
                tramoTraslado.getDenominacionTransportista());
        if (tramoTraslado.getModalidadTraslado().equals("02")){
            validateNumeroPlaca(tramoTraslado.getNumeroPlacaVehiculo());
        }

        /*validateConductorTransportePrivado(tramoTraslado.getNumeroDocumentoIdentidadConductor(),
                tramoTraslado.getTipoDocumentoIdentidadConductor());*/
    }

    private void validateModalidadTraslado(String modalidadTraslado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(modalidadTraslado)) {
            mensajeValidacion = "El campo [" + modalidadTrasladoGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(modalidadTraslado)) {
            mensajeValidacion = "El campo [" + modalidadTrasladoGuiaLabel + "] debe ser alfanumerico.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(modalidadTraslado) > 2) {
            mensajeValidacion = "El campo [" + modalidadTrasladoGuiaLabel + "] debe tener un maximo de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateFechaInicioTraslado(String fechaInicioTraslado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(fechaInicioTraslado)) {
            mensajeValidacion = "El campo [" + fechaInicioTrasladoGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (UtilFormat.fechaDate(fechaInicioTraslado) == null) {
            mensajeValidacion = "El campo [" + fechaInicioTrasladoGuiaLabel + "] debe tener el formato yyyy-MM-dd";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateConductorTransportePublico(String numeroDocumentoIdentidad,
                                                    String tipoDocumentoIdentidad, String denominacionTransportista) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(numeroDocumentoIdentidad) || StringUtils.isNotBlank(tipoDocumentoIdentidad)) {
            if (StringUtils.isBlank(numeroDocumentoIdentidad)) {
                mensajeValidacion = "El campo [" + numeroDocumentoIdentidadTransportistaGuiaLabel +
                        "] es obligatorio, cuando ingresa el campo [" + tipoDocumentoIdentidadTransportistaGuiaLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.isBlank(tipoDocumentoIdentidad)) {
                mensajeValidacion = "El campo [" + tipoDocumentoIdentidadTransportistaGuiaLabel +
                        "] es obligatorio, cuando ingresa el campo [" + numeroDocumentoIdentidadTransportistaGuiaLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            validateNumeroDocumentoIdentidadGeneric(numeroDocumentoIdentidad, numeroDocumentoIdentidadTransportistaGuiaLabel);
            validateTipoDocumentoIdentidadGeneric(tipoDocumentoIdentidad, tipoDocumentoIdentidadTransportistaGuiaLabel);
            validateDenominacion(denominacionTransportista);
        }
    }

    private void validateConductorTransportePrivado(String numeroDocumentoIdentidad,
                                                    String tipoDocumentoIdentidad) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(numeroDocumentoIdentidad) || StringUtils.isNotBlank(tipoDocumentoIdentidad)) {
            if (StringUtils.isBlank(numeroDocumentoIdentidad)) {
                mensajeValidacion = "El campo [" + numeroDocumentoIdentidadConductorGuiaLabel +
                        "] es obligatorio, cuando ingresa el campo [" + tipoDocumentoIdentidadConductorGuiaLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.isBlank(tipoDocumentoIdentidad)) {
                mensajeValidacion = "El campo [" + tipoDocumentoIdentidadConductorGuiaLabel +
                        "] es obligatorio, cuando ingresa el campo [" + numeroDocumentoIdentidadConductorGuiaLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            validateNumeroDocumentoIdentidadGeneric(numeroDocumentoIdentidad, numeroDocumentoIdentidadConductorGuiaLabel);
            validateTipoDocumentoIdentidadGeneric(tipoDocumentoIdentidad, tipoDocumentoIdentidadConductorGuiaLabel);
        }
    }

    private void validateNumeroDocumentoIdentidadGeneric(String numeroDocumentoIdentidad,
                                                         String numeroDocumentoIdentidadLabel) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (!StringUtils.isNumeric(numeroDocumentoIdentidad)) {
            mensajeValidacion = "El campo [" + numeroDocumentoIdentidadLabel + "] solo recibe digitos numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(numeroDocumentoIdentidad) > 11) {
            mensajeValidacion = "El campo [" + numeroDocumentoIdentidadLabel + "] debe a lo mas 11 digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateTipoDocumentoIdentidadGeneric(String tipoDocumentoIdentidad,
                                                       String tipoDocumentoIdentidadLabel) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (!StringUtils.isAlphanumeric(tipoDocumentoIdentidad)) {
            mensajeValidacion = "El campo [" + tipoDocumentoIdentidadLabel + "] es alfanumerico.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(tipoDocumentoIdentidad) > 1) {
            mensajeValidacion = "El campo [" + tipoDocumentoIdentidadLabel + "] es de un digito.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateDenominacion(String denominacion) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(denominacion)) {
            mensajeValidacion = "El campo [" + denominacionTransportistaGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(denominacion) > 100) {
            mensajeValidacion = "El campo [" + denominacionTransportistaGuiaLabel + "] debe tener un maximo de 100 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumeroPlaca(String numeroPlaca) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(numeroPlaca)) {
            if (StringUtils.length(numeroPlaca) > 8) {
                mensajeValidacion = "El campo [" + numeroPlacaVehiculoGuiaLabel + "] debe tener un maximo de 8 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }
}
