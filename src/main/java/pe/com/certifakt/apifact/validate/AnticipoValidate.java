package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.Anticipo;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class AnticipoValidate extends FieldsInput<Object> {

    public void validateAnticipo(Anticipo anticipo) throws ValidatorFieldsException {
        validateSerie(anticipo.getSerieAnticipo());
        validateNumero(anticipo.getNumeroAnticipo());
        validateMontoAnticipado(anticipo.getMontoAnticipado());
        validateTipoDocumentoAnticipo(anticipo.getTipoDocumentoAnticipo());
    }

    private void validateSerie(String serie)
            throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(serie)) {
            mensajeValidacion = "El campo [" + serieAnticipoLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensajeValidacion = "El campo [" + serieAnticipoLabel + "] recibe caracteres alfabeticos y numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(serie) != 4) {
            mensajeValidacion = "El campo [" + serieAnticipoLabel + "] debe ser alfanumerico de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

		/*primeraLetra = StringUtils.substring(serie, 0, 1).toUpperCase();
		if(!primeraLetra.equals("F")) {
			mensajeValidacion = "El campo ["+serieAnticipoLabel +"] debe empezar con el caracter F";
			throw new ValidatorFieldsException(mensajeValidacion);
		}*/
    }

    private void validateNumero(Integer numero) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (numero == null) {
            mensajeValidacion = "El campo [" + numeroAnticipoLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (numero < 1) {
            mensajeValidacion = "El campo [" + numeroAnticipoLabel + "] debe ser mayor que cero.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(numero.toString()) > 8) {
            mensajeValidacion = "El campo [" + numeroAnticipoLabel + "] debe tener como maximo 8 digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateMontoAnticipado(BigDecimal montoAnticipado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (montoAnticipado == null) {
            mensajeValidacion = "El campo [" + montoAnticipadoLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

    }

    private void validateTipoDocumentoAnticipo(String tipoDocumento) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoDocumento)) {
            mensajeValidacion = "El campo [" + tipoDocumentoAnticipoLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
		/*if(!(tipoDocumento.equals(ConstantesSunat.CODIGO_DOCUMENTO_RELACIONADO_FACTURA_ANTICIPO))) {
			mensajeValidacion= "EL campo ["+tipoDocumentoAnticipoLabel +"] contiene un valor No Valido. Valor "
					+ "esperado 02: Factura - emitida por anticipos.";
			throw new ValidatorFieldsException(mensajeValidacion);
		}*/
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

}
