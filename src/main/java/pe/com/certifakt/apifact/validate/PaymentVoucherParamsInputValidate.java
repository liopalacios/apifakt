package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.PaymentVoucherParamsInput;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.io.IOException;

@Component
public class PaymentVoucherParamsInputValidate extends FieldsInput<Object> {

    @Autowired
    private CompanyRepository companyRepository;

    public void validatePaymentVoucherParamsInput(PaymentVoucherParamsInput params)
            throws ValidatorFieldsException {

        validateRucActivo(params.getRucEmisor());
        validateTipoComprobante(params.getTipoComprobante());
        validateSerie(params.getSerie(), params.getTipoComprobante());
        validateNumero(params.getNumero());
        validateFechaEmisionDesde(params.getFechaEmisionDesde(), params.getFechaEmisionHasta());
        validateFechaEmisionHasta(params.getFechaEmisionDesde(), params.getFechaEmisionHasta());
        params.setSerie(params.getSerie().toUpperCase());
    }

    private void validateRucActivo(String rucEmisor) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String estado = companyRepository.getStateCompanyByRuc(rucEmisor);

        if (!estado.equals(ConstantesParameter.REGISTRO_ACTIVO)) {
            mensajeValidacion = "El ruc emisor [" + rucEmisor + "] no se encuentra habilitado para "
                    + "ejecutar operaciones al API-REST.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateTipoComprobante(String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoComprobante)) {
            mensajeValidacion = "El campo [" + tipoComprobanteToBuscarLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!(tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION)
        )) {
            mensajeValidacion = "EL campo [" + tipoComprobanteToBuscarLabel + "] contiene un valor No Valido. Valores permitidos 01: Factura, "
                    + "03: Boleta, 07: Nota Credito, 08: Nota Debito";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateSerie(String serie, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String primeraLetra;

        if (StringUtils.isBlank(serie)) {
            mensajeValidacion = "El campo [" + serieToBuscarLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensajeValidacion = "El campo [" + serieToBuscarLabel + "] recibe caracteres alfabeticos y numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(serie) != 4) {
            mensajeValidacion = "El campo [" + serieToBuscarLabel + "] debe ser alfanumerico de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        primeraLetra = StringUtils.substring(serie, 0, 1).toUpperCase();
        switch (tipoComprobante) {

            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if (!primeraLetra.equals("F")) {
                    mensajeValidacion = "El campo [" + serieToBuscarLabel + "] debe empezar con el caracter F";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
                if (!primeraLetra.equals("B")) {
                    mensajeValidacion = "El campo [" + serieToBuscarLabel + "] debe empezar con el caracter B";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:
                if (!primeraLetra.equals("F") && !primeraLetra.equals("B")) {
                    mensajeValidacion = "El campo [" + serieToBuscarLabel + "] debe empezar con el caracter F o B, "
                            + "segun el documento afectado.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
        }
    }

    private void validateNumero(Integer numero) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (numero != null) {
            if (numero < 1) {
                mensajeValidacion = "El campo [" + numeroLabel + "] debe ser mayor que cero.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(numero.toString()) > 8) {
                mensajeValidacion = "El campo [" + numeroLabel + "] debe tener como maximo 8 digitos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateFechaEmisionDesde(String fechaDesde, String fechaHasta) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(fechaDesde)) {
            if (UtilFormat.fechaDate(fechaDesde) == null) {
                mensajeValidacion = "El campo [" + fechaEmisionDesdeToBuscarLabel + "] debe tener el formato yyyy-MM-dd";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.isNotBlank(fechaHasta) && UtilFormat.fechaDate(fechaHasta) != null) {
                if (UtilFormat.fechaDate(fechaHasta).compareTo(UtilFormat.fechaDate(fechaDesde)) < 0) {
                    mensajeValidacion = "El campo [" + fechaEmisionDesdeToBuscarLabel + "] debe ser menor o igual que"
                            + " el campo [" + fechaEmisionHastaToBuscarLabel + "]";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
            }
        }
    }

    private void validateFechaEmisionHasta(String fechaDesde, String fechaHasta) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(fechaHasta)) {
            if (UtilFormat.fechaDate(fechaHasta) == null) {
                mensajeValidacion = "El campo [" + fechaEmisionHastaToBuscarLabel + "] debe tener el formato yyyy-MM-dd";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.isNotBlank(fechaDesde) && UtilFormat.fechaDate(fechaDesde) != null) {
                if (UtilFormat.fechaDate(fechaHasta).compareTo(UtilFormat.fechaDate(fechaDesde)) < 0) {
                    mensajeValidacion = "El campo [" + fechaEmisionHastaToBuscarLabel + "] debe ser mayor o igual que"
                            + " el campo [" + fechaEmisionDesdeToBuscarLabel + "]";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
            }
        }
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

}
