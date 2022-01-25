package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.GuiaItem;
import pe.com.certifakt.apifact.bean.GuiaRemision;
import pe.com.certifakt.apifact.bean.TramoTraslado;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.model.GuiaRemisionEntity;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.GuiaRemisionRepository;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GuiaRemisionValidate extends FieldsInput<Object> {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private GuiaRemisionRepository guiaRemisionRepository;
    @Autowired
    private GuiaRemisionItemValidate validateItem;
    @Autowired
    private TramoTrasladoValidate validateTramoTraslado;

    public void validateGuiaRemision(GuiaRemision guiaRemision, Boolean isEdit) throws ValidatorFieldsException {

        String identificadorDocumento;

        validateRucActivo(guiaRemision.getNumeroDocumentoIdentidadRemitente());
        validateSerie(guiaRemision.getSerie());
        validateNumero(guiaRemision.getNumero());
        identificadorDocumento = validateIdentificadorDocumento(guiaRemision.getNumeroDocumentoIdentidadRemitente(),
                guiaRemision.getSerie().toUpperCase(), guiaRemision.getNumero(), isEdit);
        validateFechaEmision(guiaRemision.getFechaEmision());
        validateHoraEmision(guiaRemision.getHoraEmision(), guiaRemision.getFechaEmision());
        validateObservaciones(guiaRemision.getObservaciones());
        validateGuiaRemisionReferenciaDadaBajaByCambioDestinatario(guiaRemision.getSerieGuiaBaja(), guiaRemision.getNumeroGuiaBaja());
        validateNumeroDAM(guiaRemision.getNumeracionDAM());
        validateNumeroManifiestoCarga(guiaRemision.getNumeracionManifiestoCarga());
        validateDocumentoAdicional(guiaRemision.getIdentificadorDocumentoRelacionado(), guiaRemision.getCodigoTipoDocumentoRelacionado());
        validateNumeroDocumentoIdentidadDestinatario(guiaRemision.getNumeroDocumentoIdentidadDestinatario());
        validateTipoDocumentoIdentidadDestinatario(guiaRemision.getTipoDocumentoIdentidadDestinatario());
        validateDenominacionDestinatario(guiaRemision.getDenominacionDestinatario());
        validateDatosProveedor(guiaRemision.getNumeroDocumentoIdentidadProveedor(),
                guiaRemision.getTipoDocumentoIdentidadProveedor(), guiaRemision.getDenominacionProveedor());
        validateMotivoTraslado(guiaRemision.getMotivoTraslado());
        validateDescripcionMotivoTraslado(guiaRemision.getDescripcionMotivoTraslado());
        validateIndicadorTransbordoProgramado(guiaRemision.getIndicadorTransbordoProgramado());
        validatePesoTotalBruto(guiaRemision.getPesoTotalBrutoBienes());
        validateUnidadMedidaPesoBruto(guiaRemision.getUnidadMedidaPesoBruto());
        validateNumeroBulto(guiaRemision.getNumeroBultos());
        validatePuntoPartidaOrLlegada(guiaRemision.getUbigeoPuntoLlegada(), guiaRemision.getDireccionPuntoLlegada(),
                ubigeoPuntoLlegadaGuiaLabel, direccionPuntoLlegadaGuiaLabel);
        validateNumeroContenedor(guiaRemision.getNumeroContenedor());
        validatePuntoPartidaOrLlegada(guiaRemision.getUbigeoPuntoPartida(), guiaRemision.getDireccionPuntoPartida(),
                ubigeoPuntoPartidaGuiaLabel, direccionPuntoPartidaGuiaLabel);
        validateCodigoPuerto(guiaRemision.getCodigoPuerto());
        validatePlacaTraslado(guiaRemision.getTramosTraslados().get(0));
        validateTramosTraslado(guiaRemision.getTramosTraslados());
        validateItems(guiaRemision.getBienesToTransportar());

        guiaRemision.setSerie(guiaRemision.getSerie().toUpperCase());
        if (guiaRemision.getObservaciones() != null && !guiaRemision.getObservaciones().isEmpty()) {
            for (int i = 0; i < guiaRemision.getObservaciones().size(); i++) {
                guiaRemision.getObservaciones().set(i, (guiaRemision.getObservaciones().get(i)).toUpperCase());
            }
        }
        if (StringUtils.isNotBlank(guiaRemision.getSerieGuiaBaja()))
            guiaRemision.setSerieGuiaBaja(guiaRemision.getSerieGuiaBaja().toUpperCase());
        guiaRemision.setIdentificadorDocumento(identificadorDocumento);
    }

    private void validatePlacaTraslado(TramoTraslado tramoTraslado) throws ValidatorFieldsException {
        String mensajeValidacion = null;
        if (tramoTraslado.getModalidadTraslado().equals("01")&&(tramoTraslado.getNumeroPlacaVehiculo()==null?"":tramoTraslado.getNumeroPlacaVehiculo()).length()<3) {
            mensajeValidacion = "La placa de traslado [" + tramoTraslado.getNumeroPlacaVehiculo() + "] No no es válida.";
            throw new ValidatorFieldsException(mensajeValidacion);
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

    private void validateSerie(String serie) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String primeraLetra;

        if (StringUtils.isBlank(serie)) {
            mensajeValidacion = "El campo [" + serieGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensajeValidacion = "El campo [" + serieGuiaLabel + "] recibe caracteres alfabeticos y numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(serie) != 4) {
            mensajeValidacion = "El campo [" + serieGuiaLabel + "] debe ser alfanumerico de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        serie = serie.toUpperCase();
        primeraLetra = StringUtils.substring(serie, 0, 1);
        if (!primeraLetra.equals("T")) {
            mensajeValidacion = "El campo [" + serieGuiaLabel + "] debe empezar con T.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

    }

    private void validateNumero(Integer numero) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (numero == null) {
            mensajeValidacion = "El campo [" + numeroGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (numero < 1) {
            mensajeValidacion = "El campo [" + numeroGuiaLabel + "] debe ser mayor que cero.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(numero.toString()) > 8) {
            mensajeValidacion = "El campo [" + numeroGuiaLabel + "] debe tener como maximo 8 digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateFechaEmision(String fechaEmision) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(fechaEmision)) {
            mensajeValidacion = "El campo [" + fechaEmisionGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (UtilFormat.fechaDate(fechaEmision) == null) {
            mensajeValidacion = "El campo [" + fechaEmisionGuiaLabel + "] debe tener el formato yyyy-MM-dd";
            throw new ValidatorFieldsException(mensajeValidacion);
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

    private void validateObservaciones(List<String> observaciones) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (observaciones != null && !observaciones.isEmpty()) {
            for (String obs : observaciones) {
                if (StringUtils.isNotBlank(obs)) {
                    if (250 < StringUtils.length(obs)) {
                        mensajeValidacion = "Las observaciones deben tener como longitud maxima de 250 caracteres.";
                        throw new ValidatorFieldsException(mensajeValidacion);
                    }
                }
            }
        }
    }

    private void validateGuiaRemisionReferenciaDadaBajaByCambioDestinatario(String serieBaja, Integer numeroBaja) throws ValidatorFieldsException {

        if (StringUtils.isNotBlank(serieBaja) || numeroBaja != null && numeroBaja > 0) {
            validateSerieBaja(serieBaja);
            validateNumeroBaja(numeroBaja);
        }
    }

    private void validateNumeroManifiestoCarga(String numeroManifiestoCarga) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isNotBlank(numeroManifiestoCarga)) {

            Pattern pattern = Pattern.compile("[0-9]{3}-[0-9]{4}-[0-9]{4}");
            Matcher matcher = pattern.matcher(numeroManifiestoCarga);
            if (!matcher.matches()) {
                mensajeValidacion = "El campo [" + numeracionManifiestoCargaLabel + "] no tiene el formato establecido para "
                        + "la numeracion de manifiesto de carga, formato[NNN-NNNN-NNNN]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateNumeroDAM(String numeroDAM) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isNotBlank(numeroDAM)) {

            Pattern pattern = Pattern.compile("[0-9]{3}-[0-9]{4}-[0-9]{2}-[0-9]{4,6}|[0-9]{4}-[0-9]{2}-[0-9]{3}-[0-9]{4}");
            Matcher matcher = pattern.matcher(numeroDAM);
            if (!matcher.matches()) {
                mensajeValidacion = "El campo [" + numeracionDAMLabel + "] no tiene el formato establecido para "
                        + "la numeracion DAM [NNN-NNNN-NN-NNNN*] ó [NNNN-NN-NNN-NNNN], *bloque numerico puede ser de tamaño 4 hasta 6.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateDocumentoAdicional(String identificadorDocumentoRelacionado,
                                            String tipoDocumentoRelacionado) throws ValidatorFieldsException {

        if (StringUtils.isNotBlank(identificadorDocumentoRelacionado) ||
                StringUtils.isNotBlank(tipoDocumentoRelacionado)) {

            validateIdentificadorDocumentoRelacionado(identificadorDocumentoRelacionado);
            validateTipoDocumentoRelacionado(tipoDocumentoRelacionado);
        }
    }

    private String validateIdentificadorDocumento(String rucEmisor, String serie,
                                                  Integer numero, Boolean isEdit) throws ValidatorFieldsException {

        StringBuilder mensajeValidacion;
        String idDocumento = rucEmisor + "-" + ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION + "-" + serie + "-" + numero;
        GuiaRemisionEntity identificadorEntity = guiaRemisionRepository.getIdentificadorDocument(idDocumento);

        if (identificadorEntity != null && !isEdit) {
            mensajeValidacion = new StringBuilder("El comprobante ya ha sido registrado [").
                    append(rucEmisorLabel).append(": ").append(rucEmisor).append(", ").
                    append(serieGuiaLabel).append(": ").append(serie).append(", ").
                    append(numeroGuiaLabel).append(": ").append(numero).append(", ").
                    append(fechaEmisionLabel).append(": ").append(identificadorEntity.getFechaEmision()).append(", ").
                    append("fecha_registro:").append(": ").append(identificadorEntity.getFechaRegistro()).append(", ").
                    append("estado: ").append(identificadorEntity.getEstado()).append("]");
            throw new ValidatorFieldsException(mensajeValidacion.toString());
        }
        return idDocumento;
    }

    private void validateSerieBaja(String serieBaja) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String primeraLetra;

        if (StringUtils.isBlank(serieBaja)) {
            mensajeValidacion = "El campo [" + serieBajaGuiaLabel + "] es obligatorio si ingresa el campo [" + numeroBajaGuiaLabel + "]";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serieBaja)) {
            mensajeValidacion = "El campo [" + serieBajaGuiaLabel + "] recibe caracteres alfabeticos y numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(serieBaja) != 4) {
            mensajeValidacion = "El campo [" + serieBajaGuiaLabel + "] debe ser alfanumerico de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        serieBaja = serieBaja.toUpperCase();
        primeraLetra = StringUtils.substring(serieBaja, 0, 1);
        if (!primeraLetra.equals("T") && !serieBaja.equals("EG01")) {
            mensajeValidacion = "El campo [" + serieBajaGuiaLabel + "] debe empezar con T o ser la serie EG01.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

    }

    private void validateNumeroBaja(Integer numeroBaja) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (numeroBaja == null) {
            mensajeValidacion = "El campo [" + numeroBajaGuiaLabel + "] es obligatorio si se ingresa el campo [" + serieBajaGuiaLabel + "]";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (numeroBaja < 1) {
            mensajeValidacion = "El campo [" + numeroBajaGuiaLabel + "] debe ser mayor que cero.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(numeroBaja.toString()) > 8) {
            mensajeValidacion = "El campo [" + numeroBajaGuiaLabel + "] debe tener como maximo 8 digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateIdentificadorDocumentoRelacionado(String identificadorDocumentoRelacionado) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(identificadorDocumentoRelacionado)) {
            mensajeValidacion = "El campo [" + identificadorDocumentoRelacionadoGuiaLabel + "] es obligatorio si ingresa el campo [" + tipoDocumentoRelacionadoGuiaLabel + "]";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
		/*
		if(!StringUtils.isAlphanumeric(identificadorDocumentoRelacionado)) {
			mensajeValidacion = "El campo ["+identificadorDocumentoRelacionadoGuiaLabel +"] recibe caracteres alfabeticos y numericos.";
			throw new ValidatorFieldsException(mensajeValidacion);
		}
		*/
        if (StringUtils.length(identificadorDocumentoRelacionado) > 20) {
            mensajeValidacion = "El campo [" + identificadorDocumentoRelacionadoGuiaLabel + "] debe ser alfanumerico de 20 caracteres como maximo.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateTipoDocumentoRelacionado(String tipoDocumentoRelacionado) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(tipoDocumentoRelacionado)) {
            mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoGuiaLabel + "] es obligatorio si ingresa el campo [" + identificadorDocumentoRelacionadoGuiaLabel + "]";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isNumeric(tipoDocumentoRelacionado)) {
            mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoGuiaLabel + "] recibe caracteres numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(tipoDocumentoRelacionado) != 2) {
            mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoGuiaLabel + "] debe ser alfanumerico de 2 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (tipoDocumentoRelacionado.equals(ConstantesSunat.NUMERACION_DAM) || tipoDocumentoRelacionado.equals(ConstantesSunat.NUMERO_MANIFIESTO_CARGA)) {
            mensajeValidacion = "El campo [" + tipoDocumentoRelacionadoGuiaLabel + "] debe ser diferente de 01[Numeración DAM] ó 04[Número de manifiesto carga].";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumeroDocumentoIdentidadDestinatario(String numeroDocumentoIdentidad) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(numeroDocumentoIdentidad)) {
            mensajeValidacion = "El campo [" + numeroIdentidadDestinatarioGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        validateNumeroDocumentoIdentidadGeneric(numeroDocumentoIdentidad, numeroIdentidadDestinatarioGuiaLabel);
    }

    private void validateTipoDocumentoIdentidadDestinatario(String tipoDocumentoIdentidad) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoDocumentoIdentidad)) {
            mensajeValidacion = "El campo [" + tipoDocumentoIdentidadDestinatarioGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        validateTipoDocumentoIdentidadGeneric(tipoDocumentoIdentidad, tipoDocumentoIdentidadDestinatarioGuiaLabel);
    }

    private void validateDenominacionDestinatario(String denominacion) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(denominacion)) {
            mensajeValidacion = "El campo [" + denominacionDestinatarioGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        validateDenominacionGeneric(denominacion, denominacionDestinatarioGuiaLabel);
    }

    private void validateDatosProveedor(String numeroDocumentoIdentidadProveedor,
                                        String tipoDocumentoIdentidadProveedor, String denominacionProveedor) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(numeroDocumentoIdentidadProveedor) ||
                StringUtils.isNotBlank(tipoDocumentoIdentidadProveedor) || StringUtils.isNotBlank(denominacionProveedor)) {

            if (StringUtils.isBlank(numeroDocumentoIdentidadProveedor)) {
                mensajeValidacion = "El campo [" + numeroDocumentoIdentidadProveedorGuiaLabel + "] es obligatorio, cuando se ingresa datos del proveedor.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            validateNumeroDocumentoIdentidadGeneric(numeroDocumentoIdentidadProveedor, numeroDocumentoIdentidadProveedorGuiaLabel);

            if (StringUtils.isBlank(tipoDocumentoIdentidadProveedor)) {
                mensajeValidacion = "El campo [" + tipoDocumentoIdentidadProveedorGuiaLabel + "] es obligatorio, cuando se ingresa datos del proveedor.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            validateTipoDocumentoIdentidadGeneric(tipoDocumentoIdentidadProveedor, tipoDocumentoIdentidadProveedorGuiaLabel);

            if (StringUtils.isBlank(denominacionProveedor)) {
                mensajeValidacion = "El campo [" + denominacionProveedorGuiaLabel + "] es obligatorio, cuando se ingresa datos del proveedor.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            validateDenominacionGeneric(denominacionProveedor, denominacionProveedorGuiaLabel);
        }
    }

    private void validateNumeroDocumentoIdentidadGeneric(String numeroDocumentoIdentidad,
                                                         String numeroDocumentoIdentidadLabel) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (!StringUtils.isNumeric(numeroDocumentoIdentidad)) {
            mensajeValidacion = "El campo [" + numeroDocumentoIdentidadLabel + "] solo recibe digitos numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(numeroDocumentoIdentidad) > 15) {
            mensajeValidacion = "El campo [" + numeroDocumentoIdentidadLabel + "] debe a lo mas 15 digitos.";
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

    private void validateDenominacionGeneric(String denominacion, String denominacionLabel) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.length(denominacion) > 100) {
            mensajeValidacion = "El campo [" + denominacionLabel + "] debe tener como maximo 100 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateMotivoTraslado(String motivoTraslado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(motivoTraslado)) {
            mensajeValidacion = "El campo [" + motivoTrasladoGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isNumeric(motivoTraslado)) {
            mensajeValidacion = "El campo [" + motivoTrasladoGuiaLabel + "] debe contener digitos numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(motivoTraslado) != 2) {
            mensajeValidacion = "El campo [" + motivoTrasladoGuiaLabel + "] debe ser de dos digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateDescripcionMotivoTraslado(String descripcionMotivoTraslado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(descripcionMotivoTraslado)) {
            if (StringUtils.length(descripcionMotivoTraslado) > 100) {
                mensajeValidacion = "El campo [" + descripcionMotivoTrasladoGuiaLabel + "] debe ser de dos digitos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateIndicadorTransbordoProgramado(Boolean indicadorTransbordoProgramado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (indicadorTransbordoProgramado == null) {
            mensajeValidacion = "El campo [" + indicadorTransbordoProgramadoGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validatePesoTotalBruto(BigDecimal pesoTotalBrutoBienes) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (pesoTotalBrutoBienes == null) {
            mensajeValidacion = "El campo [" + pesoTotalBrutoBienesGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateUnidadMedidaPesoBruto(String unidadMedidaPesoBruto) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(unidadMedidaPesoBruto)) {
            mensajeValidacion = "El campo [" + unidadMedidaPesoBrutoGuiaLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(unidadMedidaPesoBruto) > 4) {
            mensajeValidacion = "El campo [" + unidadMedidaPesoBrutoGuiaLabel + "] debe tener como maximo 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumeroBulto(Long numeroBultos) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (numeroBultos != null) {
            if (StringUtils.length(numeroBultos.toString()) > 12) {
                mensajeValidacion = "El campo [" + numeroBultosGuiaLabel + "] debe tener como maximo 12 digitos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validatePuntoPartidaOrLlegada(String ubigeo, String direccion,
                                               String ubigeoLabel, String direccionLabel) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(ubigeo) || StringUtils.isNotBlank(direccion)) {
            if (StringUtils.isBlank(ubigeo)) {
                mensajeValidacion = "El campo [" + ubigeoLabel + "] es obligatorio, cuando se ingresa [" + direccionLabel + "].";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.isBlank(direccion)) {
                mensajeValidacion = "El campo [" + direccionLabel + "] es obligatorio, cuando se ingresa [" + ubigeoLabel + "].";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(ubigeo) > 8) {
                mensajeValidacion = "El campo [" + ubigeoLabel + "] debe tener como maximo 8 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(direccion) > 100) {
                mensajeValidacion = "El campo [" + direccionLabel + "] debe tener como maximo 100 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateNumeroContenedor(String numeroContenedor) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(numeroContenedor)) {
            if (StringUtils.length(numeroContenedor) > 17) {
                mensajeValidacion = "El campo [" + numeroContenedorGuiaLabel + "] debe tener como maximo 17 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateCodigoPuerto(String codigoPuerto) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isNotBlank(codigoPuerto)) {
            if (StringUtils.length(codigoPuerto) > 3) {
                mensajeValidacion = "El campo [" + codigoPuertoGuiaLabel + "] debe tener como maximo 3 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateTramosTraslado(List<TramoTraslado> tramosTraslado) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (tramosTraslado == null || tramosTraslado.isEmpty()) {
            mensajeValidacion = "El campo [" + tramosGuiaLabel + "] es obligatorio, debe contener al menos un tramo de traslado.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        for (TramoTraslado tramo : tramosTraslado) {
            validateTramoTraslado.validateTramoValidate(tramo);
        }
    }

    private void validateItems(List<GuiaItem> items) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (items == null || items.isEmpty()) {
            mensajeValidacion = "El campo [" + itemsGuiaLabel + "] es obligatorio, debe contener al menos un bien a transportar.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        for (GuiaItem item : items) {
            validateItem.validateGuiaRemisionItem(item);
        }
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }
}
