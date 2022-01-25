package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.PaymentVoucherLine;
import pe.com.certifakt.apifact.enums.EstadoComprobanteEnum;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Component
public class UpdateBoletaValidate extends FieldsInput<Object> {

    @Autowired
    private PaymentVoucherLineValidate validateItem;
    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;
    @Autowired
    private CompanyRepository companyRepository;

	/*public PaymentVoucherEntity validateUpdateBoleta(
			PaymentVoucher boleta) throws ValidatorFieldsException {

		PaymentVoucherEntity documentToEdit;

		validateRucActivo(boleta.getRucEmisor());
		validateTipoComprobante(boleta.getTipoComprobante());
		validateSerie(boleta.getSerie(), boleta.getTipoComprobante(),
				boleta.getTipoComprobanteAfectado());
		validateNumero(boleta.getNumero());
		documentToEdit = validateIdentificadorDocumento(boleta.getRucEmisor(), boleta.getTipoComprobante(),
				boleta.getSerie().toUpperCase(), boleta.getNumero());

		validateFechaEmision(boleta.getFechaEmision());
		validateHoraEmision(boleta.getHoraEmision(), boleta.getFechaEmision(), boleta.getTipoComprobante());
		validateTipoMoneda(boleta.getCodigoMoneda());
		validateTipoDocumentoReceptor(boleta.getTipoDocumentoReceptor());
		validateNumeroDocumentoReceptor(boleta.getNumeroDocumentoReceptor(),
			boleta.getTipoDocumentoReceptor());
		validateDenominacionReceptor(boleta.getDenominacionReceptor());
		validateDomicilioFiscalEmisor(boleta.getCodigoLocalAnexoEmisor());
//		validateTipoGuiaRemision(boleta.getCodigoTipoGuiaRemision());
//		validateNumeroGuiaRemision(boleta.getSerieNumeroGuiaRemision(),
//			boleta.getCodigoTipoGuiaRemision());
		validateTipoDocumentoRelacionado(boleta.getCodigoTipoOtroDocumentoRelacionado());
		validateNumeroDocumentoRelacionado(boleta.getSerieNumeroOtroDocumentoRelacionado(),
			boleta.getCodigoTipoOtroDocumentoRelacionado());
		validateImporteTotal(boleta.getImporteTotalVenta());
		validateItems(boleta.getItems(), boleta.getTipoComprobante());

		boleta.setRucEmisor(StringUtils.trimToNull(boleta.getRucEmisor()));
		boleta.setSerie(boleta.getSerie().toUpperCase());
		boleta.setHoraEmision(StringUtils.trimToNull(boleta.getHoraEmision()));
		boleta.setCodigoMoneda(StringUtils.trimToNull(boleta.getCodigoMoneda()));
		boleta.setCodigoLocalAnexoEmisor(StringUtils.trimToNull(boleta.getCodigoLocalAnexoEmisor()));
		boleta.setDenominacionReceptor(StringUtils.trimToNull(boleta.getDenominacionReceptor()));
//		boleta.setCodigoTipoGuiaRemision(StringUtils.trimToNull(boleta.getCodigoTipoGuiaRemision()));
//		boleta.setSerieNumeroGuiaRemision(StringUtils.trimToNull(boleta.getSerieNumeroGuiaRemision()));
		boleta.setCodigoTipoOtroDocumentoRelacionado(StringUtils.trimToNull(
				boleta.getCodigoTipoOtroDocumentoRelacionado()));
		boleta.setSerieNumeroOtroDocumentoRelacionado(StringUtils.trimToNull(
				boleta.getSerieNumeroOtroDocumentoRelacionado()));
		boleta.setCodigoTipoOperacion(StringUtils.trimToNull(boleta.getCodigoTipoOperacion()));
		boleta.setMotivoNota(StringUtils.trimToNull(boleta.getMotivoNota()));

		boleta.setSerieAfectado(null);
		boleta.setNumeroAfectado(null);
		boleta.setTipoComprobanteAfectado(null);
		boleta.setMotivoNota(null);
		boleta.setCodigoTipoNotaDebito(null);
		boleta.setCodigoTipoNotaCredito(null);

		boleta.setIdentificadorDocumento(documentToEdit.getIdentificadorDocumento());

		return documentToEdit;
	}*/

    private void validateRucActivo(String rucEmisor) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String estado = companyRepository.getStateCompanyByRuc(rucEmisor);

        if (!estado.equals(ConstantesParameter.REGISTRO_ACTIVO)) {
            mensajeValidacion = "El ruc emisor [" + rucEmisor + "] No se encuentra habilitado para "
                    + "ejecutar operaciones al API-REST.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private PaymentVoucherEntity validateIdentificadorDocumento(String rucEmisor, String tipoComprobante, String serie,
                                                                Integer numero) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String idDocumento = rucEmisor + "-" + tipoComprobante + "-" + serie + "-" + numero;
        PaymentVoucherEntity identificadorEntity = paymentVoucherRepository.getIdentificadorDocument(idDocumento);

        if (identificadorEntity == null) {
            mensajeValidacion = "El comprobante [" + rucEmisorLabel + ":" + rucEmisor + "; "
                    + tipoComprobanteLabel + ":" + tipoComprobante + "; " + serieLabel + ":" + serie + "; " + numeroLabel + ":"
                    + numero + "]. No se encuentra registado en el sistema.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (identificadorEntity.getEstado().equals(EstadoComprobanteEnum.PROCESO_ENVIO.getCodigo())) {
            mensajeValidacion = "El comprobante [" + rucEmisorLabel + ":" + rucEmisor + "; "
                    + tipoComprobanteLabel + ":" + tipoComprobante + "; " + serieLabel + ":" + serie + "; " + numeroLabel + ":"
                    + numero + "] No se ha podido modificar debido que esta siendo procesado por la Sunat, "
                    + "espere unos minutos y vuelva intentarlo.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (identificadorEntity.getEstado().equals(EstadoComprobanteEnum.ANULADO.getCodigo())) {
            mensajeValidacion = "El comprobante [" + rucEmisorLabel + ":" + rucEmisor + "; "
                    + tipoComprobanteLabel + ":" + tipoComprobante + "; " + serieLabel + ":" + serie + "; " + numeroLabel + ":"
                    + numero + "] No se ha podido modificar debido que dicho comprobante ha sido previamente "
                    + "anulado.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (identificadorEntity.getEstado().equals(EstadoComprobanteEnum.PENDIENTE_ANULACION.getCodigo())) {
            mensajeValidacion = "El comprobante [" + rucEmisorLabel + ":" + rucEmisor + "; "
                    + tipoComprobanteLabel + ":" + tipoComprobante + "; " + serieLabel + ":" + serie + "; " + numeroLabel + ":"
                    + numero + "] No se ha podido modificar debido que dicho comprobante esta en pendiente de "
                    + " ser anulado.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        return identificadorEntity;
    }

    private void validateTipoComprobante(String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoComprobante)) {
            mensajeValidacion = "El campo [" + tipoComprobanteLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {
            mensajeValidacion = "EL campo [" + tipoComprobanteLabel + "] contiene un valor No Valido. Se espera 03: Boleta.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateTipoGuiaRemision(String tipoGuiaRemision) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(tipoGuiaRemision)) {
            if (!StringUtils.isAlphanumeric(tipoGuiaRemision)) {
                mensajeValidacion = "El campo [" + codigoTipoGuiaLabel + "] debe ser alfanumerico.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(StringUtils.trim(tipoGuiaRemision)) != 2) {
                mensajeValidacion = "El campo [" + codigoTipoGuiaLabel + "] debe tener 2 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateNumeroGuiaRemision(String numeroGuiaRemision, String tipoGuiaRemision) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(numeroGuiaRemision)) {
            if (StringUtils.isBlank(tipoGuiaRemision)) {
                mensajeValidacion = "El campo [" + codigoTipoGuiaLabel + "] es obligatorio, en el caso de ingresar "
                        + "el campo [" + serieNumeroGuiaLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(numeroGuiaRemision) > 30) {
                mensajeValidacion = "El campo [" + serieNumeroGuiaLabel + "] debe tener a lo mas 30 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        } else {

            if (StringUtils.isNotBlank(tipoGuiaRemision)) {
                mensajeValidacion = "Se ha ingresado el campo [" + codigoTipoGuiaLabel + "] por lo cual tambien "
                        + "debe ingresar el campo [" + serieNumeroGuiaLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateNumeroDocumentoRelacionado(String numeroDocumentoRelacionado, String tipoDocumentoRelacionado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(numeroDocumentoRelacionado)) {
            if (StringUtils.isBlank(tipoDocumentoRelacionado)) {
                mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoLabel + "] es obligatorio, en el caso de ingresar "
                        + "el campo [" + numeroDocumentoRelacionadoLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(numeroDocumentoRelacionado) > 30) {
                mensajeValidacion = "El campo [" + numeroDocumentoRelacionadoLabel + "] debe a lo mas 30 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        } else {

            if (StringUtils.isNotBlank(tipoDocumentoRelacionado)) {
                mensajeValidacion = "Se ha ingresado el campo [" + tipoDocumentoRelacionadoLabel + "] por lo cual tambien "
                        + "debe ingresar el campo [" + numeroDocumentoRelacionadoLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateTipoDocumentoRelacionado(String tipoDocumentoRelacionado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(tipoDocumentoRelacionado)) {
            if (!StringUtils.isAlphanumeric(tipoDocumentoRelacionado)) {
                mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoLabel + "] debe ser alfanumerico.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(StringUtils.trim(tipoDocumentoRelacionado)) != 2) {
                mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoLabel + "] debe tener 2 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateFechaEmision(String fechaEmision) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(fechaEmision)) {
            mensajeValidacion = "El campo [" + fechaEmisionLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (UtilFormat.fechaDate(fechaEmision) == null) {
            mensajeValidacion = "El campo [" + fechaEmisionLabel + "] debe tener el formato yyyy-MM-dd";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateTipoDocumentoReceptor(String tipoDocumentoReceptor) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoDocumentoReceptor)) {
            mensajeValidacion = "El campo [" + tipoDocumentoReceptorLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(tipoDocumentoReceptor)) {
            mensajeValidacion = "El campo [" + tipoDocumentoReceptorLabel + "] debe ser alfanumerico.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(tipoDocumentoReceptor) > 1) {
            mensajeValidacion = "El campo [" + tipoDocumentoReceptorLabel + "] debe tener un solo caracter.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumeroDocumentoReceptor(String numeroDocumentoReceptor, String tipoDocumento) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(numeroDocumentoReceptor)) {
            mensajeValidacion = "El campo [" + numeroDocumentoReceptorLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        switch (tipoDocumento) {
            case ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_DNI:
                if (!StringUtils.isNumeric(numeroDocumentoReceptor)) {
                    mensajeValidacion = "El campo [" + numeroDocumentoReceptorLabel + "] debe contener solo digitos numericos.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
                if (StringUtils.length(numeroDocumentoReceptor) != 8) {
                    mensajeValidacion = "El campo [" + numeroDocumentoReceptorLabel + "] debe tener 8 digitos.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC:
                if (!StringUtils.isNumeric(numeroDocumentoReceptor)) {
                    mensajeValidacion = "El campo [" + numeroDocumentoReceptorLabel + "] debe contener solo digitos numericos.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
                if (StringUtils.length(numeroDocumentoReceptor) != 11) {
                    mensajeValidacion = "El campo [" + numeroDocumentoReceptorLabel + "] debe tener 11 digitos.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
                break;
            default:
                if (!StringUtils.isAlphanumeric(numeroDocumentoReceptor)) {
                    mensajeValidacion = "El campo [" + numeroDocumentoReceptorLabel + "] debe contener digitos alfanumericos.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
                if (StringUtils.length(numeroDocumentoReceptor) > 15) {
                    mensajeValidacion = "El campo [" + numeroDocumentoReceptorLabel + "] debe tener 15 caracteres como maximo.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
        }
    }

    private void validateDenominacionReceptor(String denominacionReceptor) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(denominacionReceptor)) {
            mensajeValidacion = "El campo [" + denominacionReceptorLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(denominacionReceptor) > 100) {
            mensajeValidacion = "El campo [" + denominacionReceptorLabel + "] debe tener un maximo de 100 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateDomicilioFiscalEmisor(String codigoDomicilioFiscal) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(codigoDomicilioFiscal)) {

            if (!StringUtils.isAlphanumeric(codigoDomicilioFiscal)) {
                mensajeValidacion = "El campo [" + codigoLocalAnexoEmisorLabel + "] debe ser alfanumerico.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(codigoDomicilioFiscal) != 4) {
                mensajeValidacion = "El campo [" + codigoLocalAnexoEmisorLabel + "] debe tener 4 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateHoraEmision(String horaEmision, String fechaEmision, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(horaEmision)) {
            mensajeValidacion = "El campo [" + horaEmisionLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.isBlank(UtilFormat.hora(fechaEmision + " " + horaEmision))) {
            mensajeValidacion = "El campo [" + horaEmisionLabel + "] debe tener el formato hh:mm:ss";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateTipoMoneda(String tipoMoneda) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoMoneda)) {
            mensajeValidacion = "El campo [" + codigoMonedaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(tipoMoneda)) {
            mensajeValidacion = "El campo [" + codigoMonedaLabel + "] es alfanumerico.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(tipoMoneda) != 3) {
            mensajeValidacion = "El campo [" + codigoMonedaLabel + "] debe tener 3 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateImporteTotal(BigDecimal importeTotal) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (importeTotal == null) {
            mensajeValidacion = "El campo [" + importeTotalLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

    }

    private void validateSerie(String serie, String tipoComprobante,
                               String tipoComprobanteAfectado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String primeraLetra;

        if (StringUtils.isBlank(serie)) {
            mensajeValidacion = "El campo [" + serieLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensajeValidacion = "El campo [" + serieLabel + "] recibe caracteres alfabeticos y numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(serie) != 4) {
            mensajeValidacion = "El campo [" + serieLabel + "] debe ser alfanumerico de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        primeraLetra = StringUtils.substring(serie, 0, 1).toUpperCase();
        if (!primeraLetra.equals("B")) {
            mensajeValidacion = "El campo [" + serieLabel + "] debe empezar con el caracter B";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumero(Integer numero) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (numero == null) {
            mensajeValidacion = "El campo [" + numeroLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (numero < 1) {
            mensajeValidacion = "El campo [" + numeroLabel + "] debe ser mayor que cero.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(numero.toString()) > 8) {
            mensajeValidacion = "El campo [" + numeroLabel + "] debe tener como maximo 8 digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateItems(List<PaymentVoucherLine> items, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (items == null || items.isEmpty()) {
            mensajeValidacion = "El campo [" + itemsLabel + "] es obligatorio, debe contener al menos un item.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        for (PaymentVoucherLine item : items) {

            validateItem.validatePaymentVoucherLine(item, tipoComprobante, null);
        }
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }
}

