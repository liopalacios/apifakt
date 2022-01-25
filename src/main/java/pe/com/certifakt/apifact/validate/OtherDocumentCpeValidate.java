package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.DocumentCpe;
import pe.com.certifakt.apifact.bean.OtherDocumentCpe;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.model.OtherCpeEntity;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.OtherCpeRepository;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Component
public class OtherDocumentCpeValidate extends FieldsInput<Object> {

    @Autowired
    private DocumentosRelacionadosCpeValidate validateItem;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private OtherCpeRepository otherCpeRepository;

    public void validateOtherDocument(OtherDocumentCpe otroDocumento, Boolean isEdit) throws ValidatorFieldsException {

        validateRucActivo(otroDocumento.getNumeroDocumentoIdentidadEmisor());
        validateTipoComprobante(otroDocumento.getTipoComprobante());
        validateSerie(otroDocumento.getSerie(), otroDocumento.getTipoComprobante());
        validateNumero(otroDocumento.getNumero());
        validateIdentificadorDocumento(otroDocumento.getNumeroDocumentoIdentidadEmisor(),
                otroDocumento.getSerie().toUpperCase(),
                otroDocumento.getNumero(),
                otroDocumento.getTipoComprobante(), isEdit);
        validateFechaEmision(otroDocumento.getFechaEmision());
        validateHoraEmision(otroDocumento.getHoraEmision(), otroDocumento.getFechaEmision());

        validateNumeroDocumentoIdentidadReceptor(otroDocumento.getNumeroDocumentoIdentidadReceptor(), otroDocumento.getTipoComprobante(),
                otroDocumento.getTipoDocumentoIdentidadReceptor());
        validateTipoDocumentoIdentidadReceptor(otroDocumento.getTipoDocumentoIdentidadReceptor(), otroDocumento.getTipoComprobante());
        validateNombreComercialReceptor(otroDocumento.getNombreComercialReceptor(), otroDocumento.getTipoComprobante());
        validateDenominacionReceptor(otroDocumento.getDenominacionReceptor(), otroDocumento.getTipoComprobante());
        validateUbicacionReceptor(otroDocumento.getUbigeoDomicilioFiscalReceptor(),
                otroDocumento.getDireccionCompletaDomicilioFiscalReceptor(),
                otroDocumento.getUrbanizacionDomicilioFiscalReceptor(),
                otroDocumento.getDepartamentoDomicilioFiscalReceptor(),
                otroDocumento.getProvinciaDomicilioFiscalReceptor(),
                otroDocumento.getDistritoDomicilioFiscalReceptor(),
                otroDocumento.getCodigoPaisDomicilioFiscalReceptor(),
                otroDocumento.getTipoComprobante());

        validateRegimen(otroDocumento.getRegimen(), otroDocumento.getTipoComprobante());
//		validateTasa(otroDocumento.getTasa(), otroDocumento.getTipoComprobante());
        validateMontoTotalRetenidoPercibido(otroDocumento.getImporteTotalRetenidoPercibido(), otroDocumento.getTipoComprobante());
        validateMontoTotalPagadoCobrado(otroDocumento.getImporteTotalPagadoCobrado(), otroDocumento.getTipoComprobante());
        validateMontoRedondeoImporteTotal(otroDocumento.getMontoRedondeoImporteTotal());
        validateObservaciones(otroDocumento.getObservaciones());
        validateCodigoMoneda(otroDocumento.getCodigoMoneda(), otroDocumento.getTipoComprobante());

        validateDocumentosRelacionados(otroDocumento.getDocumentosRelacionados(), otroDocumento.getTipoComprobante());

        System.out.println("Imprimiendo items retencion 2");
        System.out.println(otroDocumento.getDocumentosRelacionados());

        otroDocumento.setSerie(otroDocumento.getSerie().toUpperCase());
        if (StringUtils.isNotBlank(otroDocumento.getObservaciones()))
            otroDocumento.setObservaciones(otroDocumento.getObservaciones().trim());
        BigDecimal tasa = null;
        if (otroDocumento.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION)) {
            switch (otroDocumento.getRegimen()) {
                case "01":
                    tasa = new BigDecimal("3");
                    break;
                case "02":
                    tasa = new BigDecimal("6");
                    break;
            }
        } else {
            switch (otroDocumento.getRegimen()) {
                case "01":
                    tasa = new BigDecimal("2");
                    break;
                case "02":
                    tasa = new BigDecimal("1");
                    break;
                case "03":
                    tasa = new BigDecimal("0.5");
                    break;
            }
        }
        otroDocumento.setTasa(tasa);
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

    private void validateTipoComprobante(String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoComprobante)) {
            mensajeValidacion = "El campo [" + tipoComprobanteLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!(tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_PERCEPTION))) {
            mensajeValidacion = "EL campo [" + tipoComprobanteLabel + "] contiene un valor No Válido. Valores permitidos 20: Retención, "
                    + "40: Percepción";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateSerie(String serie, String tipoComprobante) throws ValidatorFieldsException {

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
        serie = serie.toUpperCase();
        primeraLetra = StringUtils.substring(serie, 0, 1);
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION)) {
            if (!primeraLetra.equals("R") && !StringUtils.isNumeric(serie)) {
                mensajeValidacion = "El campo [" + serieLabel + "] puede ser númerico ó empezar con el caracter R.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        } else {
            if (!primeraLetra.equals("P") && !StringUtils.isNumeric(serie)) {
                mensajeValidacion = "El campo [" + serieLabel + "] puede ser númerico ó empezar con el caracter P.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
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

    private void validateIdentificadorDocumento(String rucEmisor, String serie,
                                                Integer numero, String tipoComprobante, Boolean isEdit) throws ValidatorFieldsException {

        StringBuilder mensajeValidacion;
        String idDocumento = rucEmisor + "-" + tipoComprobante + "-" + serie + "-" + numero;
        OtherCpeEntity identificadorEntity = otherCpeRepository.getIdentificadorDocument(idDocumento);

        if (identificadorEntity != null && !isEdit) {
            mensajeValidacion = new StringBuilder("El comprobante ya ha sido registrado [").
                    append(rucEmisorLabel).append(": ").append(rucEmisor).append(", ").
                    append(serieLabel).append(": ").append(serie).append(", ").
                    append(numeroLabel).append(": ").append(numero).append(", ").
                    append(fechaEmisionLabel).append(": ").append(identificadorEntity.getFechaEmision()).append(", ").
                    append("fecha_registro:").append(": ").append(identificadorEntity.getFechaRegistro()).append(", ").
                    append("estado: ").append(identificadorEntity.getEstado()).append("]");
            throw new ValidatorFieldsException(mensajeValidacion.toString());
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

    private void validateUbicacionReceptor(String ubigeo, String direccion, String urbanizacionDomicilioFiscal,
                                           String departamentoDomicilioFiscal, String provinciaDomicilioFiscal, String distritoDomicilioFiscal,
                                           String codigoPaisDomicilioFiscal, String tipoComprobante) throws ValidatorFieldsException {
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam;
        String mensajeValidacion = null;
        if (StringUtils.length(ubigeo) != 0 && StringUtils.length(ubigeo) != 6) {
            etiquetaParam = (isRetencion) ? ubigeoDomicilioFiscalReceptorRetencionLabel : ubigeoDomicilioFiscalReceptorPercepcionLabel;
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener 6 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(departamentoDomicilioFiscal) > 30) {
            etiquetaParam = (isRetencion) ? departamentoDomicilioFiscalReceptorRetencionLabel : departamentoDomicilioFiscalReceptorPercepcionLabel;
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener como maximo 30 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(provinciaDomicilioFiscal) > 30) {
            etiquetaParam = (isRetencion) ? provinciaDomicilioFiscalReceptorRetencionLabel : provinciaDomicilioFiscalReceptorPercepcionLabel;
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener como maximo 30 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(distritoDomicilioFiscal) > 30) {
            etiquetaParam = (isRetencion) ? distritoDomicilioFiscalReceptorRetencionLabel : distritoDomicilioFiscalReceptorPercepcionLabel;
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener como maximo 30 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(codigoPaisDomicilioFiscal) != 0 && !codigoPaisDomicilioFiscal.equals("PE")) {
            etiquetaParam = (isRetencion) ? codigoPaisDomicilioFiscalReceptorRetencionLabel : codigoPaisDomicilioFiscalReceptorPercepcionLabel;
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe ser PE.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(direccion) > 100) {
            etiquetaParam = (isRetencion) ? direccionCompletaDomicilioFiscalReceptorRetencionLabel : direccionCompletaDomicilioFiscalReceptorPercepcionLabel;
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener como maximo 100 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(urbanizacionDomicilioFiscal) > 30) {
            etiquetaParam = (isRetencion) ? urbanizacionDomicilioFiscalReceptorRetencionLabel : urbanizacionDomicilioFiscalReceptorPercepcionLabel;
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener como maximo 30 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumeroDocumentoIdentidadReceptor(String numeroDocumentoIdentidad, String tipoComprobante,
                                                          String tipoDocumentoReceptor) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? numeroDocumentoIdentidadReceptorRetencionLabel : numeroDocumentoIdentidadReceptorPercepcionLabel;
        if (StringUtils.isBlank(numeroDocumentoIdentidad)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (isRetencion) {
            if (!StringUtils.isNumeric(numeroDocumentoIdentidad)) {
                mensajeValidacion = "El campo [" + etiquetaParam + "] debe contener solo numeros.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(numeroDocumentoIdentidad) != 11) {
                mensajeValidacion = "El campo [" + etiquetaParam + "] debe ser de 11 digitos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        } else {

            switch (tipoDocumentoReceptor) {
                case ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_DNI:
                    if (!StringUtils.isNumeric(numeroDocumentoIdentidad)) {
                        mensajeValidacion = "El campo [" + etiquetaParam + "] debe contener solo digitos numericos.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }
                    if (StringUtils.length(numeroDocumentoIdentidad) != 8) {
                        mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener 8 digitos.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }
                    break;
                case ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC:
                    if (!StringUtils.isNumeric(numeroDocumentoIdentidad)) {
                        mensajeValidacion = "El campo [" + etiquetaParam + "] debe contener solo digitos numericos.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }
                    if (StringUtils.length(numeroDocumentoIdentidad) != 11) {
                        mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener 11 digitos.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }
                    break;
                default:
                    if (!tipoDocumentoReceptor.equals(ConstantesSunat.TIPO_DOCUMENTO_NO_DOMI_SIN_RUC)) {
                        if (!StringUtils.isAlphanumeric(numeroDocumentoIdentidad)) {
                            mensajeValidacion = "El campo [" + etiquetaParam + "] debe contener digitos alfanumericos.";
                            throw new ValidatorFieldsException(mensajeValidacion);
                        }
                        if (StringUtils.length(numeroDocumentoIdentidad) > 15) {
                            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener 15 caracteres como maximo.";
                            throw new ValidatorFieldsException(mensajeValidacion);
                        }
                    }
            }
			/*
			validateNumeroDocumentoIdentidadGeneric(numeroDocumentoIdentidad, etiquetaParam);
			if(StringUtils.length(numeroDocumentoIdentidad) > 15) {
				mensajeValidacion = "El campo ["+etiquetaParam+"] puede tener a lo mas 15 caracteres.";
				throw new ValidatorFieldsException(mensajeValidacion);
			}
			*/
        }
    }

    private void validateTipoDocumentoIdentidadReceptor(String tipoDocumentoIdentidad, String tipoComprobante) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? tipoDocumentoIdentidadReceptorRetencionLabel : tipoDocumentoIdentidadReceptorPercepcionLabel;
        if (StringUtils.isBlank(tipoDocumentoIdentidad)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        validateTipoDocumentoIdentidadGeneric(tipoDocumentoIdentidad, etiquetaParam);
        if (isRetencion) {
            if (!tipoDocumentoIdentidad.equals(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC)) {
                mensajeValidacion = "El campo [" + etiquetaParam + "] es diferente de 6: RUC.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateNombreComercialReceptor(String nombre, String tipoComprobante) throws ValidatorFieldsException {
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? nombreComercialReceptorRetencionLabel : nombreComercialReceptorPercepcionLabel;
        if (StringUtils.isNotBlank(nombre)) {
            validateDenominacionOrNombreComercial(nombre, etiquetaParam);
        }
    }

    private void validateHoraEmision(String horaEmision, String fechaEmision) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(horaEmision)) {
            if (StringUtils.isBlank(UtilFormat.hora(fechaEmision + " " + horaEmision))) {
                mensajeValidacion = "El campo [" + horaEmisionLabel + "] debe tener el formato hh:mm:ss";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateDenominacionReceptor(String denominacion, String tipoComprobante) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? denominacionReceptorRetencionLabel : denominacionReceptorPercepcionLabel;
        if (StringUtils.isBlank(denominacion)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        validateDenominacionOrNombreComercial(denominacion, etiquetaParam);
    }

    private void validateNumeroDocumentoIdentidadGeneric(String numeroDocumentoIdentidad,
                                                         String numeroDocumentoIdentidadLabel) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (!StringUtils.isAlphanumeric(numeroDocumentoIdentidad)) {
            mensajeValidacion = "El campo [" + numeroDocumentoIdentidadLabel + "] solo recibe digitos alfabeticos y númericos.";
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
            mensajeValidacion = "El campo [" + tipoDocumentoIdentidadLabel + "] es de un caracter.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateDenominacionOrNombreComercial(String denominacion, String denominacionLabel) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        if (StringUtils.length(denominacion) > 1500) {
            mensajeValidacion = "El campo [" + denominacionLabel + "] debe tener como maximo 1500 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateRegimen(String regimen, String tipoComprobante) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? regimenRetencionLabel : regimenPercepcionLabel;
        if (!StringUtils.isNumeric(regimen)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener caracteres númericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(regimen) != 2) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener 2 digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (isRetencion) {
            if (!regimen.equals("01") && !regimen.equals("02")) {
                mensajeValidacion = "El valor del campo [" + etiquetaParam + "] no es valido, se espera cualquiera de los siguientes valores:"
                        + "01: Tasa 3%; 02: 6%";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        } else {
            if (!regimen.equals("01") && !regimen.equals("02") && !regimen.equals("03")) {
                mensajeValidacion = "El valor del campo [" + etiquetaParam + "] no es valido, se espera cualquiera de los siguientes valores: "
                        + "01: Percepcion venta interna; 02: Percepcion a la adquisicion de combutible; 03: Percepción realizada al agente de percepción con tasa especial.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateTasa(BigDecimal tasa, String tipoComprobante) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? tasaRetencionLabel : tasaPercepcionLabel;
        if (tasa == null) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateObservaciones(String observacion) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        if (StringUtils.length(observacion) > 250) {
            mensajeValidacion = "El campo [" + observacionesOtroCpeLabel + "] debe tener como maximo 250 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateMontoTotalRetenidoPercibido(BigDecimal montoTotalRetenidoPercibido, String tipoComprobante) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? importeTotalRetenidoLabel : importeTotalPercibidoLabel;
        if (montoTotalRetenidoPercibido == null) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateMontoTotalPagadoCobrado(BigDecimal montoTotalPagadoCobrado, String tipoComprobante) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? importeTotalPagadoLabel : importeTotalCobradoLabel;
        if (montoTotalPagadoCobrado == null) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateMontoRedondeoImporteTotal(BigDecimal montoRedondeo) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        String etiquetaParam = montoRedondeoImporteTotalLabel;
        if (montoRedondeo != null) {
            if (montoRedondeo.abs().compareTo(BigDecimal.ONE) > 0) {
                mensajeValidacion = "El valor absoluto del campo [" + etiquetaParam + "] no debe ser mayor que uno.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateCodigoMoneda(String tipoMoneda, String tipoComprobante) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? codigoMonedaRetencionLabel : codigoMonedaPercepcionLabel;
        if (StringUtils.isBlank(tipoMoneda)) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(tipoMoneda) > 3) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] debe tener como maximo 3 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateDocumentosRelacionados(List<DocumentCpe> items, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean isRetencion = (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        String etiquetaParam = (isRetencion) ? documentosRelacionadosRetencionLabel : documentosRelacionadosPercepcionLabel;

        if (items == null || items.isEmpty()) {
            mensajeValidacion = "El campo [" + etiquetaParam + "] es obligatorio, debe contener al menos un item.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        for (DocumentCpe item : items) {
            validateItem.validateDocumentosRelacionados(item, tipoComprobante);
        }
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }


}
