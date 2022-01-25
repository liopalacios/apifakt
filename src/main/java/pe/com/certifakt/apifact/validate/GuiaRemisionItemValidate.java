package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.GuiaItem;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class GuiaRemisionItemValidate extends FieldsInput<Object> {

    public void validateGuiaRemisionItem(GuiaItem item) throws ValidatorFieldsException {

        validateCantidad(item.getCantidad());
        validateUnidadMedida(item.getUnidadMedida());
        validateDescripcion(item.getDescripcion());
        validateCodigoItem(item.getCodigoItem());
        //validatePrecioItem(item.getPrecioItem());
    }

    private void validateCantidad(BigDecimal cantidad) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (cantidad == null) {
            mensajeValidacion = "El campo [" + cantidadGuiaLabel + "] es obligatorio";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

    }

    private void validateUnidadMedida(String unidadMedida) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(unidadMedida)) {
            mensajeValidacion = "El campo [" + unidadMedidaGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(unidadMedida)) {
            mensajeValidacion = "El campo [" + unidadMedidaGuiaLabel + "] debe ser alfanumerico.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(unidadMedida) > 4) {
            mensajeValidacion = "El campo [" + unidadMedidaGuiaLabel + "] debe tener un maximo de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateDescripcion(String descripcion) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(descripcion)) {
            mensajeValidacion = "El campo [" + descripcionGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(descripcion) > 250) {
            mensajeValidacion = "El campo [" + descripcionGuiaLabel + "] debe tener un maximo de 250 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateCodigoItem(String codigoItem) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(codigoItem)) {
            if (StringUtils.length(codigoItem) > 16) {
                mensajeValidacion = "El campo [" + codigoItemGuiaLabel + "] debe tener un maximo de 16 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validatePrecioItem(BigDecimal precioItem) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (precioItem == null) {
            mensajeValidacion = "El campo [" + precioItemGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }
}
