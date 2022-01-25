package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.Comprobante;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.util.List;

@Component
public class ComprobanteValidate extends FieldsInput<Object> {

    @Autowired
    private CompanyRepository companyRepository;

    public void validateListaComprobantes(
            List<Comprobante> comprobantes, String rucEmisor) throws ValidatorFieldsException {

        validateRucActivo(rucEmisor);
        for (Comprobante comprobante : comprobantes) {
            validateComprobante(comprobante);
            comprobante.setSerie(comprobante.getSerie().toUpperCase());
        }
    }

    private void validateComprobante(Comprobante comprobante) throws ValidatorFieldsException {

        validateTipoComprobante(comprobante.getTipoComprobante());
        validateSerie(comprobante.getSerie(), comprobante.getTipoComprobante());
        validateNumero(comprobante.getNumero());
    }

    private void validateTipoComprobante(String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoComprobante)) {
            mensajeValidacion = "El campo [" + tipoComprobanteForBusquedaByIdDocumentosLabel
                    + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!(tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION)
        )) {
            mensajeValidacion = "EL campo [" + tipoComprobanteForBusquedaByIdDocumentosLabel
                    + "] contiene un valor No Valido. Valores permitidos 01: Factura, "
                    + "03: Boleta, 07: Nota Credito, 08: Nota Debito";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateSerie(String serie, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(serie)) {
            mensajeValidacion = "El campo [" + serieForBusquedaByIdDocumentosLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensajeValidacion = "El campo [" + serieForBusquedaByIdDocumentosLabel + "] recibe caracteres alfabeticos y numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(serie) != 4) {
            mensajeValidacion = "El campo [" + serieForBusquedaByIdDocumentosLabel + "] debe ser alfanumerico de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumero(Integer numero) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (numero != null) {
            if (numero < 1) {
                mensajeValidacion = "El campo [" + numeroForBusquedaByIdDocumentosLabel + "] debe ser mayor que cero.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(numero.toString()) > 8) {
                mensajeValidacion = "El campo [" + numeroForBusquedaByIdDocumentosLabel + "] debe tener como maximo 8 digitos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateRucActivo(String rucEmisor) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String estado = companyRepository.getStateCompanyByRuc(rucEmisor);

        if (!estado.equals(ConstantesParameter.REGISTRO_ACTIVO)) {
            mensajeValidacion = "El ruc emisor [" + rucEmisor + "] No se encuentra habilitado para "
                    + "ejecutar operaciones al API-REST.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

}
