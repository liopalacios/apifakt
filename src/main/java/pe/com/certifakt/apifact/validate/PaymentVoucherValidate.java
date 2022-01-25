package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.Anticipo;
import pe.com.certifakt.apifact.bean.PaymentVoucher;
import pe.com.certifakt.apifact.bean.PaymentVoucherLine;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class PaymentVoucherValidate extends FieldsInput<Object> {

    @Autowired
    private PaymentVoucherLineValidate validateItem;
    @Autowired
    private AnticipoValidate validateAnticipo;
    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;
    @Autowired
    private CompanyRepository companyRepository;

    private static final BigDecimal MONTO_MINIMO_BOLETA_TO_DATOS_CLIENTE = new BigDecimal("700");

    public void validatePaymentVoucher(PaymentVoucher paymentVoucher, Boolean isEdit) throws ValidatorFieldsException {

        boolean datosReceptorObligatorio;
        String identificadorDocumento;

        validateRucActivo(paymentVoucher.getRucEmisor());
        validateTipoComprobante(paymentVoucher.getTipoComprobante());
        if (paymentVoucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) ||
                paymentVoucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {

            validateTipoComprobanteAfectado(paymentVoucher.getTipoComprobanteAfectado());
            validateSerieAfectado(paymentVoucher.getSerieAfectado());
            validateNumeroAfectado(paymentVoucher.getNumeroAfectado());
            validateCodigoTipoNotaCredito(paymentVoucher.getTipoComprobante(),
                    paymentVoucher.getCodigoTipoNotaCredito());
            validateCodigoTipoNotaDebito(paymentVoucher.getTipoComprobante(),
                    paymentVoucher.getCodigoTipoNotaDebito());
            validateMotivoNota(paymentVoucher.getMotivoNota());
        } else {
            // FIXME  Solucion temporal
            if (paymentVoucher.getCodigoTipoOperacion() != null) {

                if (paymentVoucher.getCodigoTipoOperacion().trim().length() == 4) {
                    paymentVoucher.setCodigoTipoOperacionCatalogo51(paymentVoucher.getCodigoTipoOperacion());

                } else {
                    switch (paymentVoucher.getCodigoTipoOperacion()) {
                        case "01":
                        case "04":
                            paymentVoucher.setCodigoTipoOperacionCatalogo51("0101");
                            break;
                        case "02":
                            paymentVoucher.setCodigoTipoOperacionCatalogo51("0200");
                            break;
                        default:
                            paymentVoucher.setCodigoTipoOperacionCatalogo51("0101");
                            break;
                    }
                }

            } else {
                paymentVoucher.setCodigoTipoOperacionCatalogo51("0101");
            }
        }
        validateSerie(paymentVoucher.getSerie(), paymentVoucher.getTipoComprobante(),
                paymentVoucher.getTipoComprobanteAfectado());
        validateNumero(paymentVoucher.getNumero());

        identificadorDocumento = validateIdentificadorDocumento(paymentVoucher.getRucEmisor(), paymentVoucher.getTipoComprobante(),
                paymentVoucher.getSerie().toUpperCase(), paymentVoucher.getNumero(), isEdit);

//		validateFechaEmision(paymentVoucher.getFechaEmision(), paymentVoucher.getTipoComprobante());
        validateHoraEmision(paymentVoucher.getHoraEmision(), paymentVoucher.getFechaEmision());
        validateTipoMoneda(paymentVoucher.getCodigoMoneda());
        validateImporteTotal(paymentVoucher.getImporteTotalVenta());

        datosReceptorObligatorio = validateObligatoriedadDatosCliente(paymentVoucher.getTipoComprobante(), paymentVoucher.getTipoComprobanteAfectado(), paymentVoucher.getImporteTotalVenta());

        validateTipoDocumentoReceptor(paymentVoucher.getTipoDocumentoReceptor(), datosReceptorObligatorio);
        validateNumeroDocumentoReceptor(paymentVoucher.getNumeroDocumentoReceptor(),
                paymentVoucher.getTipoDocumentoReceptor(), datosReceptorObligatorio);
        validateDenominacionReceptor(paymentVoucher.getDenominacionReceptor(), datosReceptorObligatorio);

        validateDomicilioFiscalEmisor(paymentVoucher.getCodigoLocalAnexoEmisor());

        validateTipoDocumentoRelacionado(paymentVoucher.getCodigoTipoOtroDocumentoRelacionado());
        validateNumeroDocumentoRelacionado(paymentVoucher.getSerieNumeroOtroDocumentoRelacionado(),
                paymentVoucher.getCodigoTipoOtroDocumentoRelacionado());
        validateAnticipos(paymentVoucher.getAnticipos());
        validateItems(paymentVoucher.getItems(), paymentVoucher.getTipoComprobante(), paymentVoucher.getUblVersion());


        if (paymentVoucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
            validateTotalIsc(paymentVoucher.getTotalValorBaseIsc(), paymentVoucher.getTotalIsc(), paymentVoucher.getItems());
            validateTotalGratuita(paymentVoucher.getTotalValorVentaGratuita(), paymentVoucher.getTotalImpOperGratuita(), paymentVoucher.getItems());
            validateTotalGravada(paymentVoucher.getTotalValorVentaGravada(), paymentVoucher.getTotalIgv(), paymentVoucher.getItems());
            validateTotalOtrosTributos(paymentVoucher.getTotalValorBaseOtrosTributos(), paymentVoucher.getTotalOtrostributos(), paymentVoucher.getItems());
        }

        paymentVoucher.setRucEmisor(StringUtils.trimToNull(paymentVoucher.getRucEmisor()));
        paymentVoucher.setSerie(paymentVoucher.getSerie().toUpperCase());
        paymentVoucher.setHoraEmision(StringUtils.trimToNull(paymentVoucher.getHoraEmision()));
        paymentVoucher.setCodigoMoneda(StringUtils.trimToNull(paymentVoucher.getCodigoMoneda()));
        paymentVoucher.setCodigoLocalAnexoEmisor(StringUtils.trimToNull(paymentVoucher.getCodigoLocalAnexoEmisor()));
        paymentVoucher.setDenominacionReceptor(StringUtils.trimToNull(paymentVoucher.getDenominacionReceptor()));

        paymentVoucher.setCodigoTipoOtroDocumentoRelacionado(StringUtils.trimToNull(
                paymentVoucher.getCodigoTipoOtroDocumentoRelacionado()));
        paymentVoucher.setSerieNumeroOtroDocumentoRelacionado(StringUtils.trimToNull(
                paymentVoucher.getSerieNumeroOtroDocumentoRelacionado()));
        paymentVoucher.setCodigoTipoOperacion(StringUtils.trimToNull(paymentVoucher.getCodigoTipoOperacion()));
        paymentVoucher.setMotivoNota(StringUtils.trimToNull(paymentVoucher.getMotivoNota()));
        paymentVoucher.setIdentificadorDocumento(identificadorDocumento);
        switch (paymentVoucher.getTipoComprobante()) {

            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
            case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:

                paymentVoucher.setSerieAfectado(null);
                paymentVoucher.setNumeroAfectado(null);
                paymentVoucher.setTipoComprobanteAfectado(null);
                paymentVoucher.setMotivoNota(null);
                paymentVoucher.setCodigoTipoNotaDebito(null);
                paymentVoucher.setCodigoTipoNotaCredito(null);

				/*if(paymentVoucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {
					paymentVoucher.setAnticipos(null);
				}*/
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:

                if (paymentVoucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    paymentVoucher.setTotalValorVentaGratuita(null);
                }
                paymentVoucher.setDescuentoGlobales(null);
                paymentVoucher.setSerieAfectado(paymentVoucher.getSerieAfectado().toUpperCase());
                //paymentVoucher.setAnticipos(null);

                if (paymentVoucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
                    paymentVoucher.setCodigoTipoNotaCredito(null);
                    paymentVoucher.setTotalDescuento(null);
                } else {
                    paymentVoucher.setCodigoTipoNotaDebito(null);
                }
                break;
            default:
        }


        if (StringUtils.isBlank(paymentVoucher.getDenominacionReceptor())) {
            paymentVoucher.setDenominacionReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucher.getNumeroDocumentoReceptor())) {
            paymentVoucher.setNumeroDocumentoReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucher.getTipoDocumentoReceptor())) {
            paymentVoucher.setTipoDocumentoReceptor("-");
        }

        validateDetracciones(paymentVoucher);
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

    private String validateIdentificadorDocumento(String rucEmisor, String tipoComprobante, String serie,
                                                  Integer numero, Boolean isEdit) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String idDocumento = rucEmisor + "-" + tipoComprobante + "-" + serie + "-" + numero;
        PaymentVoucherEntity identificadorEntity = paymentVoucherRepository.getIdentificadorDocument(idDocumento);

        if (identificadorEntity != null && !isEdit) {
            mensajeValidacion = "El comprobante ya ha sido registrado [" + rucEmisorLabel + ":" + rucEmisor + "; "
                    + tipoComprobanteLabel + ":" + tipoComprobante + "; " + serieLabel + ":" + serie + "; " + numeroLabel + ":"
                    + numero + "; " + fechaEmisionLabel + ":" + identificadorEntity.getFechaEmision() + "; fecha_registro:"
                    + identificadorEntity.getFechaRegistro() + "; estado:" + identificadorEntity.getEstado() + "].";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        return idDocumento;
    }

    private void validateTipoComprobante(String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoComprobante)) {
            mensajeValidacion = "El campo [" + tipoComprobanteLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!(tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION)
        )) {
            mensajeValidacion = "EL campo [" + tipoComprobanteLabel + "] contiene un valor No Valido. Valores permitidos 01: Factura, "
                    + "03: Boleta, 07: Nota Credito, 08: Nota Debito";
            throw new ValidatorFieldsException(mensajeValidacion);
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

    private void validateFechaEmision(String fechaEmision, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaEmisionDate;

        if (StringUtils.isBlank(fechaEmision)) {
            mensajeValidacion = "El campo [" + fechaEmisionLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (UtilFormat.fechaDate(fechaEmision) == null) {
            mensajeValidacion = "El campo [" + fechaEmisionLabel + "] debe tener el formato yyyy-MM-dd";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        try {
            switch (tipoComprobante) {
                case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
                    fechaEmisionDate = formatter.parse(fechaEmision);
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    if (calendar.getTime().compareTo(fechaEmisionDate) > 0) {
                        mensajeValidacion = "El campo [" + fechaEmisionLabel + "] no debe tener más de 7 días";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }
                    break;

            }
        } catch (ParseException e) {
            throw new ValidatorFieldsException(e.getMessage());
        }
    }

    private void validateTipoDocumentoReceptor(String tipoDocumentoReceptor, boolean datosReceptorObligatorio) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoDocumentoReceptor) && datosReceptorObligatorio) {
            mensajeValidacion = "El campo [" + tipoDocumentoReceptorLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.isNotBlank(tipoDocumentoReceptor) && !StringUtils.isAlphanumeric(tipoDocumentoReceptor)) {
            mensajeValidacion = "El campo [" + tipoDocumentoReceptorLabel + "] debe ser alfanumerico.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.isNotBlank(tipoDocumentoReceptor) && StringUtils.length(tipoDocumentoReceptor) > 1) {
            mensajeValidacion = "El campo [" + tipoDocumentoReceptorLabel + "] debe tener un solo caracter.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumeroDocumentoReceptor(String numeroDocumentoReceptor,
                                                 String tipoDocumentoReceptor, boolean datosReceptorObligatorio) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (datosReceptorObligatorio) {
            if (StringUtils.isBlank(numeroDocumentoReceptor)) {
                mensajeValidacion = "El campo [" + numeroDocumentoReceptorLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        } else {
            if (StringUtils.isNotBlank(numeroDocumentoReceptor) || StringUtils.isNotBlank(tipoDocumentoReceptor)) {
                if (StringUtils.isBlank(numeroDocumentoReceptor)) {
                    mensajeValidacion = "El campo [" + numeroDocumentoReceptorLabel + "] es obligatorio, si ingresa el campo [" + tipoDocumentoReceptorLabel + "]";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
                if (StringUtils.isBlank(tipoDocumentoReceptor)) {
                    mensajeValidacion = "El campo [" + tipoDocumentoReceptorLabel + "] es obligatorio, si ingresa el campo [" + numeroDocumentoReceptorLabel + "]";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
            } else {
                return;
            }
        }

        switch (tipoDocumentoReceptor) {
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
                if (!tipoDocumentoReceptor.equals(ConstantesSunat.TIPO_DOCUMENTO_NO_DOMI_SIN_RUC)) {
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
    }

    private void validateDenominacionReceptor(String denominacionReceptor, boolean datosReceptorObligatorio) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (datosReceptorObligatorio) {
            if (StringUtils.isBlank(denominacionReceptor)) {
                mensajeValidacion = "El campo [" + denominacionReceptorLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
        if (StringUtils.isNotBlank(denominacionReceptor) && StringUtils.length(denominacionReceptor) > 100) {
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

    private void validateHoraEmision(String horaEmision, String fechaEmision) throws ValidatorFieldsException {

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

    private void validateTotalIsc(BigDecimal montoBaseIsc, BigDecimal montoIsc, List<PaymentVoucherLine> items) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean existeAlmenosUno = false;
        if (montoBaseIsc != null || montoIsc != null) {
            if (montoBaseIsc == null) {
                mensajeValidacion = "El campo [" + totalValorBaseIscLabel + "] es obligatorio, si ingresa valor en [" + totalIscLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (montoIsc == null) {
                mensajeValidacion = "El campo [" + totalIscLabel + "] es obligatorio, si ingresa valor en [" + totalValorBaseIscLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            for (PaymentVoucherLine line : items) {
                existeAlmenosUno = validateItem.validateOperacionISC(line.getMontoBaseIsc(), line.getIsc(), line.getPorcentajeIsc(), line.getCodigoTipoCalculoISC());
                if (existeAlmenosUno) {
                    break;
                }
            }
            if (!existeAlmenosUno) {
                mensajeValidacion = "Debe ingresar al menos un item con datos del tributo ISC.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateTotalGravada(BigDecimal montoBaseGravada, BigDecimal montoIgv, List<PaymentVoucherLine> items) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean existeAlmenosUno = false;
        if (montoBaseGravada != null || montoIgv != null) {
            if (montoBaseGravada == null) {
                mensajeValidacion = "El campo [" + totalValorVentaGravadaLabel + "] es obligatorio, si ingresa valor en [" + totalIgvLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (montoIgv == null) {
                mensajeValidacion = "El campo [" + totalIgvLabel + "] es obligatorio, si ingresa valor en [" + totalValorVentaGravadaLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            for (PaymentVoucherLine line : items) {
                existeAlmenosUno = validateItem.validateOperacionGravada(line.getMontoBaseIgv(), line.getIgv(), line.getPorcentajeIgv(), line.getCodigoTipoAfectacionIGV());
                if (existeAlmenosUno) {
                    break;
                }
            }
            if (!existeAlmenosUno) {
                mensajeValidacion = "Debe ingresar al menos un item con datos del tributo con afectación gravada.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateTotalOtrosTributos(BigDecimal montoBaseOtrosTributos, BigDecimal montoOtrosTributos, List<PaymentVoucherLine> items) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean existeAlmenosUno = false;
        if (montoBaseOtrosTributos != null || montoOtrosTributos != null) {
            if (montoBaseOtrosTributos == null) {
                mensajeValidacion = "El campo [" + totalValorBaseOtrosTributosLabel + "] es obligatorio, si ingresa valor en [" + totalOtrostributosLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (montoOtrosTributos == null) {
                mensajeValidacion = "El campo [" + totalOtrostributosLabel + "] es obligatorio, si ingresa valor en [" + totalValorBaseOtrosTributosLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            for (PaymentVoucherLine line : items) {
                existeAlmenosUno = validateItem.validateOperacionOtrosTributos(line.getMontoBaseOtrosTributos(), line.getOtrosTributos(), line.getPorcentajeOtrosTributos());
                if (existeAlmenosUno) {
                    break;
                }
            }
            if (!existeAlmenosUno) {
                mensajeValidacion = "Debe ingresar al menos un item con datos de Otros tributos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateTotalGratuita(BigDecimal montoBaseGratuita, BigDecimal montoGratuita, List<PaymentVoucherLine> items) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean existeAlmenosUno = false;
        boolean existeProductoGratuito = false;

        if (montoBaseGratuita != null || montoGratuita != null) {
            if(montoBaseGratuita.doubleValue() > 0){
                if (montoBaseGratuita == null) {
                    mensajeValidacion = "El campo [" + totalValorVentaGratuitaLabel + "] es obligatorio, si ingresa valor en [" + totalImpOperGratuitaLabel + "]";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
            /*if (montoGratuita == null) {
                mensajeValidacion = "El campo [" + totalImpOperGratuitaLabel + "] es obligatorio, si ingresa valor en [" + totalValorVentaGratuitaLabel + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }*/
                for (PaymentVoucherLine line : items) {
                    existeProductoGratuito = false;
                    existeProductoGratuito = validateItem.validateOperacionGratuita(line.getMontoBaseGratuito(), line.getImpuestoVentaGratuita(), line.getPorcentajeTributoVentaGratuita(), line.getValorReferencialUnitario());
                    if (existeProductoGratuito) {
                        existeAlmenosUno = true;
                        line.setValorUnitario(BigDecimal.ZERO);
                        //line.setPrecioVentaUnitario(null);
                    }
                }
                if (!existeAlmenosUno) {
                    mensajeValidacion = "Debe ingresar al menos un item con datos de venta gratuita.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
            }

        }
    }

    private void validateSerie(String serie, String tipoComprobante, String tipoComprobanteAfectado) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(serie)) {
            mensajeValidacion = "El campo [" + serieLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensajeValidacion = "El campo [" + serieLabel + "] recibe caracteres del alfabeto y números.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(serie) != 4) {
            mensajeValidacion = "El campo [" + serieLabel + "] debe ser alfanumerico de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        switch (tipoComprobante) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                validateSerieFactura(serie);
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
                validateSerieBoleta(serie);
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:
                if (tipoComprobanteAfectado.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
                    validateSerieFactura(serie);
                } else if (tipoComprobanteAfectado.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {
                    validateSerieBoleta(serie);
                }
        }
    }

    private void validateSerieFactura(String serie) throws ValidatorFieldsException {
        String primeraLetra;
        String mensajeValidacion = null;

        primeraLetra = StringUtils.substring(serie, 0, 1).toUpperCase();
        if (!primeraLetra.equals("F") && !StringUtils.isNumeric(serie)) {
            mensajeValidacion = "El campo [" + serieLabel + "] puede ser númerico ó empezar con el caracter F.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateSerieBoleta(String serie) throws ValidatorFieldsException {
        String primeraLetra;
        String mensajeValidacion = null;

        primeraLetra = StringUtils.substring(serie, 0, 1).toUpperCase();
        if (!primeraLetra.equals("B") && !StringUtils.isNumeric(serie)) {
            mensajeValidacion = "El campo [" + serieLabel + "] puede ser númerico ó empezar con el caracter B.";
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

    private void validateSerieAfectado(String serieAfectado) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(serieAfectado)) {
            mensajeValidacion = "El campo [" + serieAfectadoLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serieAfectado)) {
            mensajeValidacion = "El campo [" + serieAfectadoLabel + "] recibe caracteres alfabeticos y numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumeroAfectado(Integer numeroAfectado) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (numeroAfectado == null) {
            mensajeValidacion = "El campo [" + numeroAfectadoLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (numeroAfectado < 1) {
            mensajeValidacion = "El campo [" + numeroAfectadoLabel + "] debe ser mayor que cero.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateTipoComprobanteAfectado(String tipoComprobanteAfectado) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(tipoComprobanteAfectado)) {
            mensajeValidacion = "El campo [" + tipoComprobanteAfectadoLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isNumeric(tipoComprobanteAfectado)) {
            mensajeValidacion = "El campo [" + tipoComprobanteAfectadoLabel + "] debe tener "
                    + "caracteres numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!(tipoComprobanteAfectado.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobanteAfectado.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA) ||
                tipoComprobanteAfectado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) ||
                tipoComprobanteAfectado.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO))) {

            mensajeValidacion = "El campo [" + tipoComprobanteAfectadoLabel + "] puede corresponder "
                    + "a alguno de los siguientes comprobantes: 01 Factura, 03 Boleta de venta.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateCodigoTipoNotaCredito(String tipoComprobante, String tipoNotaCredito) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)) {

            if (StringUtils.isBlank(tipoNotaCredito)) {
                mensajeValidacion = "El campo [" + tipoNotaCreditoLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (!StringUtils.isNumeric(tipoNotaCredito)) {
                mensajeValidacion = "El campo [" + tipoNotaCreditoLabel + "] debe tener "
                        + "caracteres numericos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(tipoNotaCredito) != 2) {
                mensajeValidacion = "El campo [" + tipoNotaCreditoLabel + "] debe tener 2 caracteres numericos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateCodigoTipoNotaDebito(String tipoComprobante, String tipoNotaDebito) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {

            if (StringUtils.isBlank(tipoNotaDebito)) {
                mensajeValidacion = "El campo [" + tipoNotaDebitoLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (!StringUtils.isNumeric(tipoNotaDebito)) {
                mensajeValidacion = "El campo [" + tipoNotaDebitoLabel + "] debe tener "
                        + "caracteres numericos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(tipoNotaDebito) != 2) {
                mensajeValidacion = "El campo [" + tipoNotaDebitoLabel + "] debe tener 2 caracteres numericos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateMotivoNota(String motivo) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(motivo)) {
            mensajeValidacion = "El campo [" + motivoNotaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (250 < StringUtils.length(motivo)) {
            mensajeValidacion = "El campo [" + motivoNotaLabel + "] debe tener como longitud maxima de 250 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private boolean validateObligatoriedadDatosCliente(String tipoComprobante, String tipoComprobanteAfectado, BigDecimal importeTotal) {

        boolean datosObligatorio = true;
        switch (tipoComprobante) {
            case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
                if ((importeTotal.compareTo(MONTO_MINIMO_BOLETA_TO_DATOS_CLIENTE) <= 0)) {
                    datosObligatorio = false;
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:
                if (tipoComprobanteAfectado.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA) &&
                        (importeTotal.compareTo(MONTO_MINIMO_BOLETA_TO_DATOS_CLIENTE) <= 0)) {
                    datosObligatorio = false;
                }
                break;
            default:
                break;
        }

        return datosObligatorio;
    }

    private void validateItems(List<PaymentVoucherLine> items, String tipoComprobante, String ublVersion) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (items == null || items.isEmpty()) {
            mensajeValidacion = "El campo [" + itemsLabel + "] es obligatorio, debe contener al menos un item.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        for (PaymentVoucherLine item : items) {

            validateItem.validatePaymentVoucherLine(item, tipoComprobante, ublVersion);
        }
    }

    private void validateAnticipos(List<Anticipo> anticipos) throws ValidatorFieldsException {

        int correlativo = 1;
        if (anticipos != null && !anticipos.isEmpty()) {
            for (Anticipo anticipo : anticipos) {
                validateAnticipo.validateAnticipo(anticipo);
                if (correlativo < 10) {
                    anticipo.setIdentificadorPago("0" + correlativo);
                } else {
                    anticipo.setIdentificadorPago(Integer.toString(correlativo));
                }
                correlativo++;
            }
        }

    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

    private void validateDetracciones(PaymentVoucher paymentVoucher) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean existeCodigoBien = false;

        if (paymentVoucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)
                || paymentVoucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {

            if (paymentVoucher.getCodigoBienDetraccion() != null) {
                existeCodigoBien = true;
            }

            if (existeCodigoBien) {

                if (paymentVoucher.getCodigoTipoOperacion().equals("1001") || paymentVoucher.getCodigoTipoOperacion().equals("1004")) {

                    if (StringUtils.length(StringUtils.trim(paymentVoucher.getCodigoBienDetraccion())) != 3) {
                        mensajeValidacion = "El campo [" + codigoBienDetraccionLabel + "] debe tener 3 digitos.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }

                    if (paymentVoucher.getCuentaFinancieraBeneficiario() == null) {

                        mensajeValidacion = "El campo [" + cuentaFinancieraBeneficiarioLabel + "] es obligatorio.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }

                    if (StringUtils.length(StringUtils.trim(paymentVoucher.getCuentaFinancieraBeneficiario())) > 100) {
                        mensajeValidacion = "El campo [" + cuentaFinancieraBeneficiarioLabel
                                + "] debe tener como maximo 100 digitos.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }

                    if (paymentVoucher.getMontoDetraccion() == null) {
                        mensajeValidacion = "El campo [" + montoDetraccionLabel + "] es obligarorio.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }

                    if (!StringUtils.isAlphanumeric(paymentVoucher.getCodigoMedioPago())) {
                        mensajeValidacion = "El campo [" + codigoMedioPagoLabel
                                + "] recibe caracteres alfabeticos y numericos.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }

                    if (StringUtils.length(StringUtils.trim(paymentVoucher.getCodigoMedioPago())) != 3) {
                        mensajeValidacion = "El campo [" + codigoMedioPagoLabel + "] debe tener 3 digitos.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }

                    if (paymentVoucher.getPorcentajeDetraccion() == null) {
                        mensajeValidacion = "El campo [" + porcentajeDetraccionLabel + "] es obligarorio.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }

                    if (paymentVoucher.getCodigoBienDetraccion().equals("027")) {

                        for (PaymentVoucherLine item : paymentVoucher.getItems()) {

                            if (StringUtils.isBlank(item.getDetalleViajeDetraccion())) {
                                mensajeValidacion = "El campo [" + detalleViajeDetraccionLabel + "] es obligarorio, para codigo bien 027.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }

                            if (StringUtils.isBlank(item.getUbigeoOrigenDetraccion())) {
                                mensajeValidacion = "El campo [" + ubigeoOrigenDetraccionLabel + "] es obligarorio, para codigo bien 027.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }

                            if (StringUtils.isBlank(item.getDireccionOrigenDetraccion())) {
                                mensajeValidacion = "El campo [" + direccionOrigenDetraccionLabel + "] es obligarorio, para codigo bien 027.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }

                            if (StringUtils.isBlank(item.getUbigeoDestinoDetraccion())) {
                                mensajeValidacion = "El campo [" + ubigeoDestinoDetraccionLabel + "] es obligarorio, para codigo bien 027.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }

                            if (StringUtils.isBlank(item.getDireccionDestinoDetraccion())) {
                                mensajeValidacion = "El campo [" + direccionDestinoDetraccionLabel + "] es obligarorio, para codigo bien 027.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }

                            if (item.getUbigeoOrigenDetraccion().length() != 6) {
                                mensajeValidacion = "El campo [" + ubigeoOrigenDetraccionLabel + "] debe tener 6 caracteres.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }

                            if (item.getUbigeoDestinoDetraccion().length() != 6) {
                                mensajeValidacion = "El campo [" + ubigeoDestinoDetraccionLabel + "] debe tener 6 caracteres.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }

                            if (item.getValorServicioTransporte() == null) {
                                mensajeValidacion = "El campo [" + valorServicioTransporteLabel + "] es obligarorio, para codigo bien 027.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }

                            if (item.getValorCargaEfectiva() == null) {
                                mensajeValidacion = "El campo [" + valorCargaEfectivaLabel + "] es obligarorio, para codigo bien 027.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }

                            if (item.getValorCargaUtil() == null) {
                                mensajeValidacion = "El campo [" + valorCargaUtilLabel + "] es obligarorio, para codigo bien 027.";
                                throw new ValidatorFieldsException(mensajeValidacion);
                            }
                        }
                    }

                } else {

                    mensajeValidacion = "Las Detracciones, no corresponden al valor de Tipo Operacion Ingresado.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
            }
        }
    }
}
