package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.DocumentCpe;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class DocumentosRelacionadosCpeValidate extends FieldsInput<Object> {

    public void validateDocumentosRelacionados(DocumentCpe otroDocumento, String tipoComprobante) throws ValidatorFieldsException {


        validateTipoDocumentoRelacionado(otroDocumento.getTipoDocumentoRelacionado(), tipoComprobante);
        validateSerieDocumentoRelacionado(otroDocumento.getSerieDocumentoRelacionado());
        validateNumeroDocumentoRelacionado(otroDocumento.getNumeroDocumentoRelacionado());
        validateFechaEmisionDocumentoRelacionado(otroDocumento.getFechaEmisionDocumentoRelacionado());
        validateImporteTotalDocumentoRelacionado(otroDocumento.getImporteTotalDocumentoRelacionado());
        validateCodigoMoneda(otroDocumento.getMonedaDocumentoRelacionado());
        validateFechaPagoCobro(otroDocumento.getFechaPagoCobro(), otroDocumento.getTipoDocumentoRelacionado(), tipoComprobante);
        validateNumeroPagoCobro(otroDocumento.getNumeroPagoCobro(), otroDocumento.getTipoDocumentoRelacionado(), tipoComprobante);
        validateImportePagoSinRetencionCobro(otroDocumento.getImportePagoSinRetencionCobro(), otroDocumento.getTipoDocumentoRelacionado(), tipoComprobante);
        validateCodigoMonedaPagoCobro(otroDocumento.getMonedaPagoCobro(), otroDocumento.getMonedaDocumentoRelacionado(), tipoComprobante, otroDocumento.getTipoDocumentoRelacionado());
        validateImporteRetenidoPercibido(otroDocumento.getImporteRetenidoPercibido(), tipoComprobante);
        validateCodigoMonedaImporteRetenidoPercibido(otroDocumento.getMonedaImporteRetenidoPercibido(), tipoComprobante);
        validateFechaRetencionPercepcion(otroDocumento.getFechaRetencionPercepcion(), tipoComprobante);
        validateImporteTotalToPagarCobrar(otroDocumento.getImporteTotalToPagarCobrar(), tipoComprobante);
        validateMonedaImporteTotalToPagarCobrar(otroDocumento.getMonedaImporteTotalToPagarCobrar(), tipoComprobante);
        //boolean isObligatorioTipoCambio = validateMonedaReferenciaTipoCambio(otroDocumento.getMonedaReferenciaTipoCambio(), otroDocumento.getTipoDocumentoRelacionado(), otroDocumento.getMonedaDocumentoRelacionado());
        validateMonedaReferenciaTipoCambio(otroDocumento.getMonedaReferenciaTipoCambio(), otroDocumento.getTipoDocumentoRelacionado(), otroDocumento.getMonedaDocumentoRelacionado());
        /*if (!isObligatorioTipoCambio) {

            otroDocumento.setMonedaReferenciaTipoCambio(null);
            otroDocumento.setMonedaObjetivoTasaCambio(null);
            otroDocumento.setTipoCambio(null);
            otroDocumento.setFechaCambio(null);
        } else {*/

            validateMonedaObjetivoTasaCambio(otroDocumento.getMonedaObjetivoTasaCambio(), otroDocumento.getTipoDocumentoRelacionado(), tipoComprobante, true);
            validateTipoCambio(otroDocumento.getTipoCambio(), otroDocumento.getTipoDocumentoRelacionado(), otroDocumento.getMonedaDocumentoRelacionado(), true);
            validateFechaCambio(otroDocumento.getFechaCambio(), otroDocumento.getTipoDocumentoRelacionado(), otroDocumento.getMonedaDocumentoRelacionado(), true);
        //}
    }


    private void validateTipoDocumentoRelacionado(String tipoDocumentoRelacionado, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoDocumentoRelacionado)) {
            mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoOtroCpeLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION)) {
            switch (tipoDocumentoRelacionado) {
                case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:
                case ConstantesSunat.TIPO_DOCUMENTO_TICKET_MAQ_REGISTRADORA:
                case ConstantesSunat.TIPO_DOCUMENTO_RETENTION:
                    break;
                default:
                    mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoOtroCpeLabel + "] puede ser :" +
                            ConstantesSunat.TIPO_DOCUMENTO_FACTURA + ", " + ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + ", " +
                            ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + ", " + ConstantesSunat.TIPO_DOCUMENTO_TICKET_MAQ_REGISTRADORA + " y " +
                            ConstantesSunat.TIPO_DOCUMENTO_RETENTION;
                    break;
            }
        } else {
            switch (tipoDocumentoRelacionado) {
                case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
                case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:
                case ConstantesSunat.TIPO_DOCUMENTO_TICKET_MAQ_REGISTRADORA:
                case ConstantesSunat.TIPO_DOCUMENTO_PERCEPTION:
                    break;
                default:
                    mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoOtroCpeLabel + "] puede ser :" +
                            ConstantesSunat.TIPO_DOCUMENTO_FACTURA + ", " + ConstantesSunat.TIPO_DOCUMENTO_BOLETA + ", " +
                            ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO + ", " + ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO + ", " +
                            ConstantesSunat.TIPO_DOCUMENTO_TICKET_MAQ_REGISTRADORA + " y " + ConstantesSunat.TIPO_DOCUMENTO_PERCEPTION;
                    break;
            }
        }
        if (mensajeValidacion != null) {
            throw new ValidatorFieldsException(mensajeValidacion);
        }

    }

    private void validateSerieDocumentoRelacionado(String serie) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(serie)) {
            mensajeValidacion = "El campo [" + serieDocumentoRelacionadoOtroCpeLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensajeValidacion = "El campo [" + serieDocumentoRelacionadoOtroCpeLabel + "] recibe caracteres alfabeticos y numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(serie) != 4) {
            mensajeValidacion = "El campo [" + serieDocumentoRelacionadoOtroCpeLabel + "] debe ser alfanumerico de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumeroDocumentoRelacionado(Integer numero) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (numero == null) {
            mensajeValidacion = "El campo [" + numeroDocumentoRelacionadoOtroCpeLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (numero < 1) {
            mensajeValidacion = "El campo [" + numeroDocumentoRelacionadoOtroCpeLabel + "] debe ser mayor que cero.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(numero.toString()) > 8) {
            mensajeValidacion = "El campo [" + numeroDocumentoRelacionadoOtroCpeLabel + "] debe tener como maximo 8 digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateFechaEmisionDocumentoRelacionado(String fechaEmision) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(fechaEmision)) {
            mensajeValidacion = "El campo [" + fechaEmisionDocumentoRelacionadoOtroCpeLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (UtilFormat.fechaDate(fechaEmision) == null) {
            mensajeValidacion = "El campo [" + fechaEmisionDocumentoRelacionadoOtroCpeLabel + "] debe tener el formato yyyy-MM-dd";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateImporteTotalDocumentoRelacionado(BigDecimal importeTotalDocumentoRelacionado) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        if (importeTotalDocumentoRelacionado == null) {
            mensajeValidacion = "El campo [" + importeTotalDocumentoRelacionadoOtroCpeLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateCodigoMoneda(String tipoMoneda) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        if (StringUtils.length(tipoMoneda) == 0 || StringUtils.length(tipoMoneda) > 3) {
            mensajeValidacion = "El campo [" + monedaDocumentoRelacionadoOtroCpeLabel + "] es obligatorio y debe tener como maximo 3 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateFechaPagoCobro(String fechaPagoCobro, String tipoDocumentoRelacionado, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? fechaPagoCobroRetencionLabel : fechaPagoCobroPercepcionLabel;

        if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) && StringUtils.isBlank(fechaPagoCobro)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) && UtilFormat.fechaDate(fechaPagoCobro) == null) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener el formato yyyy-MM-dd";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumeroPagoCobro(String numero, String tipoDocumentoRelacionado, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? numeroPagoCobroRetencionLabel : numeroPagoCobroPercepcionLabel;

        if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) && StringUtils.isBlank(numero)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) && !StringUtils.isNumeric(numero)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener " + "caracteres numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) && StringUtils.length(numero) > 9) {

            mensajeValidacion = "El campo [" + etiquetaParam + "] puede contener hasta 9 digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateImportePagoSinRetencionCobro(BigDecimal importePagoSinRetencionCobro,
                                                      String tipoDocumentoRelacionado, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? importePagoSinRetencionCobroRetencionLabel : importePagoSinRetencionCobroPercepcionLabel;

        if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) && importePagoSinRetencionCobro == null) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateCodigoMonedaPagoCobro(String tipoMoneda, String monedaTipoDocumentoRelacionado,
                                               String tipoComprobante, String tipoDocumentoRelacionado)
            throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? monedaPagoCobroRetencionLabel : monedaPagoCobroPercepcionLabel;

        if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) && (StringUtils.length(tipoMoneda) == 0 || StringUtils.length(tipoMoneda) > 3)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio y debe tener como maximo 3 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (tipoMoneda != null && !tipoMoneda.equals(monedaTipoDocumentoRelacionado)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe ser igual al campo [" + monedaDocumentoRelacionadoOtroCpeLabel + "]";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateImporteRetenidoPercibido(BigDecimal importeRetenidoPercibidoRetencion, String tipoComprobante) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? importeRetenidoPercibidoRetencionLabel : importeRetenidoPercibidoPercepcionLabel;

        if (importeRetenidoPercibidoRetencion == null) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateCodigoMonedaImporteRetenidoPercibido(String tipoMoneda, String tipoComprobante)
            throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? monedaImporteRetenidoPercibidoRetencionLabel : monedaImporteRetenidoPercibidoPercepcionLabel;

        if (StringUtils.isBlank(tipoMoneda)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!tipoMoneda.equals("PEN")) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe ser igual al PEN";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateFechaRetencionPercepcion(String fechaRetencionPercepcion, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? fechaRetencionPercepcionRetencionLabel : fechaRetencionPercepcionPercepcionLabel;

        if (StringUtils.isBlank(fechaRetencionPercepcion)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (UtilFormat.fechaDate(fechaRetencionPercepcion) == null) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener el formato yyyy-MM-dd";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateImporteTotalToPagarCobrar(BigDecimal importeTotalToPagarCobrar, String tipoComprobante) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? importeTotalToPagarCobrarRetencionLabel : importeTotalToPagarCobrarPercepcionLabel;

        if (importeTotalToPagarCobrar == null) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateMonedaImporteTotalToPagarCobrar(String tipoMoneda, String tipoComprobante)
            throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? monedaImporteTotalToPagarCobrarRetencionLabel : monedaImporteTotalToPagarCobrarPercepcionLabel;

        if (StringUtils.isBlank(tipoMoneda)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!tipoMoneda.equals("PEN")) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe ser igual al PEN";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateMonedaReferenciaTipoCambio(String tipoMoneda, String tipoDocumentoRelacionado, String tipoMonedaDocumentoRelacionado)
            throws ValidatorFieldsException {

        String mensajeValidacion = null;
        //boolean isObligatorio = false;

        if (StringUtils.isNotBlank(tipoMoneda)) {
            if (StringUtils.length(tipoMoneda) > 3) {
                mensajeValidacion = "El campo [" + monedaReferenciaTipoCambioOtroCpeLabel + "] es de hasta 3 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
        if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)) {
            if (!tipoMonedaDocumentoRelacionado.equals("PEN")) {
                //isObligatorio = true;
                if (StringUtils.isBlank(tipoMoneda)) {
                    mensajeValidacion = "El campo [" + monedaReferenciaTipoCambioOtroCpeLabel + "] debe contener información de la moneda de referencia del tipo de cambio.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
            }

            if (tipoMoneda != null && !tipoMoneda.equals(tipoMonedaDocumentoRelacionado)) {
                mensajeValidacion = "El campo [" + monedaReferenciaTipoCambioOtroCpeLabel + "] debe ser igual que el campo [" + monedaDocumentoRelacionadoOtroCpeLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }

        //return isObligatorio;
    }

    private void validateMonedaObjetivoTasaCambio(String tipoMoneda, String tipoDocumentoRelacionado, String tipoComprobante, boolean isObligatorio)
            throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (isObligatorio) {
            if (StringUtils.isBlank(tipoMoneda)) {
                mensajeValidacion = "El campo [" + monedaObjetivoTasaCambioOtroCpeLabel + "] es obligatorio";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION)) {
            if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) &&
                    (StringUtils.isBlank(tipoMoneda) || !tipoMoneda.equals("PEN"))) {
                mensajeValidacion = "El campo [" + monedaObjetivoTasaCambioOtroCpeLabel + "] debe PEN";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        } else {
            if (StringUtils.isNotBlank(tipoMoneda) && !tipoMoneda.equals("PEN")) {
                mensajeValidacion = "El campo [" + monedaObjetivoTasaCambioOtroCpeLabel + "] debe PEN";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateTipoCambio(BigDecimal tipoCambio, String tipoDocumentoRelacionado, String tipoMonedaDocumentoRelacionado, boolean isObligatorio)
            throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (isObligatorio) {
            if (tipoCambio == null) {
                mensajeValidacion = "El campo [" + tipoCambioOtroCpeLabel + "] es obligatorio";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
        if (!tipoDocumentoRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)) {
            if (!tipoMonedaDocumentoRelacionado.equals("PEN") && tipoCambio == null) {
                mensajeValidacion = "El campo [" + tipoCambioOtroCpeLabel + "] debe ser ingresado.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateFechaCambio(String fechaCambio, String tipoDocumentoRelacionado, String tipoMonedaDocumentoRelacionado, boolean isObligatorio) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (isObligatorio) {
            if (StringUtils.isBlank(fechaCambio)) {
                mensajeValidacion = "El campo [" + fechaCambioOtroCpeLabel + "] es obligatorio";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (UtilFormat.fechaDate(fechaCambio) == null) {
                mensajeValidacion = "El campo [" + fechaCambioOtroCpeLabel + "] debe tener el formato yyyy-MM-dd";
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
