package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.VoucherAnnular;
import pe.com.certifakt.apifact.enums.EstadoComprobanteEnum;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.model.GuiaRemisionEntity;
import pe.com.certifakt.apifact.model.ParameterEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.GuiaRemisionRepository;
import pe.com.certifakt.apifact.repository.ParameterRepository;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class VoucherAnnularValidate extends FieldsInput<Object> {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;
    @Autowired
    private ParameterRepository parameterRepository;
    @Autowired
    private GuiaRemisionRepository guiaRemisionRepository;

    public void validateVoucherAnnular(List<VoucherAnnular> documentos, String rucEmisor) throws ValidatorFieldsException {

        ParameterEntity parametroEntity = parameterRepository.findByName(ConstantesParameter.RANGO_DIAS_BAJA_DOCUMENTOS);
        Integer rangoFechaAceptable = Integer.parseInt(parametroEntity.getValue());
        String fechaEmisionValidado;

        validateRucActivo(rucEmisor);
        for (VoucherAnnular documento : documentos) {
            System.out.println("Documento ANULAR  "+documento.getRucEmisor());
            boolean isDocumentFacturaOrNotaAsociada = false;
            String tipoComprobanteRelacionado;
            validateTipoComprobante(documento.getTipoComprobante());
            validateSerie(documento.getSerie());
            validateNumero(documento.getNumero());
            tipoComprobanteRelacionado = validateTipoComprobanteRelacionado(documento.getTipoComprobante(), documento.getTipoComprobanteRelacionado());
            documento.setTipoComprobanteRelacionado(tipoComprobanteRelacionado);
            validateMotivoAnulacion(documento.getMotivoAnulacion());

            String identificadorDocumento = rucEmisor + "-" + documento.getTipoComprobante() + "-" +
                    documento.getSerie().toUpperCase() + "-" + documento.getNumero();

            if (documento.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
                isDocumentFacturaOrNotaAsociada = true;
            } else {
                if (documento.getTipoComprobanteRelacionado() != null &&
                        documento.getTipoComprobanteRelacionado().equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
                    isDocumentFacturaOrNotaAsociada = true;
                }
            }
            fechaEmisionValidado = validaFechaBajaByDocumento(
                    identificadorDocumento,
                    rangoFechaAceptable,
                    isDocumentFacturaOrNotaAsociada,
                    documento.getTipoComprobante());

            documento.setFechaEmision(fechaEmisionValidado);
            documento.setSerie(documento.getSerie().toUpperCase());
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

    private void validateTipoComprobante(String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoComprobante)) {
            mensajeValidacion = "El campo [" + tipoComprobanteToAnularLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!(tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION)
        )) {
            mensajeValidacion = "EL campo [" + tipoComprobanteToAnularLabel + "] contiene un valor No Valido. "
                    + "Valores permitidos 01: Factura, 03: Boleta, 07: Nota Credito, 08: Nota Debito";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateSerie(String serie) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(serie)) {
            mensajeValidacion = "El campo [" + serieToAnularLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensajeValidacion = "El campo [" + serieToAnularLabel + "] recibe caracteres alfabeticos y numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(serie) != 4) {
            mensajeValidacion = "El campo [" + serieToAnularLabel + "] Debe ser alfanumerico de 4 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private void validateNumero(Integer numero) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (numero == null) {
            mensajeValidacion = "El campo [" + numeroToAnularLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (numero < 1) {
            mensajeValidacion = "El campo [" + numeroToAnularLabel + "] debe ser mayor que cero.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(numero.toString()) > 8) {
            mensajeValidacion = "El campo [" + numeroToAnularLabel + "] debe tener como maximo 8 digitos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    private String validateTipoComprobanteRelacionado(String tipoComprobante, String tipoComprobanteRelacionado) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
            if (StringUtils.isBlank(tipoComprobanteRelacionado)) {
                mensajeValidacion = "El campo [" + tipoComprobanteRelacionadoToAnularLabel + "] es obligatorio, cuando "
                        + tipoComprobanteToAnularLabel + " es 07: Nota Credito, 08: Nota Debito";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (!(tipoComprobanteRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
                    || tipoComprobanteRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA))) {
                mensajeValidacion = "EL campo [" + tipoComprobanteRelacionadoToAnularLabel + "] contiene un valor No Valido. "
                        + "Valores permitidos 01: Factura, 03: Boleta.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        } else {
            return null;
        }

        return tipoComprobanteRelacionado;
    }

    private void validateMotivoAnulacion(String motivoAnulacion) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(motivoAnulacion)) {
            mensajeValidacion = "El campo [" + motivoToAnularLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(motivoAnulacion) > 100) {
            mensajeValidacion = "El campo [" + motivoToAnularLabel + "] debe tener como longitud maxima 100 caracteres.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }


    private String validaFechaBajaByDocumento(String identificadorDocumento,
                                              Integer rangoDiasAceptable, boolean isDocumentFacturaOrNotaAsociada, String tipoDocumento) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String fechaEmision = null;
        EstadoComprobanteEnum estadoComprobante = null;
        System.out.println("tipo de documento: "+tipoDocumento.getClass().getSimpleName());

        PaymentVoucherEntity entity = paymentVoucherRepository.getIdentificadorDocument(identificadorDocumento);
        GuiaRemisionEntity guiaEntity = new GuiaRemisionEntity();
        if (entity == null){
            guiaEntity = guiaRemisionRepository.getIdentificadorDocument(identificadorDocumento);
        }


        if (entity == null && guiaEntity == null) {

            mensajeValidacion = "El comprobante no ha sido anulado, "
                    + "Por este medio solo se podrá anular los documentos que previamente han "
                    + "sido registrados desde el API-REST.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if(tipoDocumento.contains("09")){
            System.out.println("tipo de documento 1: "+tipoDocumento);
            estadoComprobante = EstadoComprobanteEnum.getEstadoComprobante(guiaEntity.getEstado());
        }else{
            System.out.println("tipo de documento 2: "+tipoDocumento);
            estadoComprobante = EstadoComprobanteEnum.getEstadoComprobante(entity.getEstado());
        }
        switch (estadoComprobante) {

            case REGISTRADO:
                if (isDocumentFacturaOrNotaAsociada) {
                    mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                            + "debido a que para anular una factura o nota asociada a una factura,"
                            + " este debe estar previamente acpetada por la Sunat.";
                }
            case ACEPTADO:
            case ACEPTADO_ADVERTENCIA:
                break;
            case ACEPTADO_POR_VERIFICAR:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido a que dicho comprobante necesita verificar su resultado por la Sunat.";
                break;
            case PENDIENTE_ANULACION:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido a que dicho comprobante se en encuentra en pendiente de anulacion.";
                break;
            /*case ANULADO:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido que dicho comprobante ya se encuentra anulado por la Sunat.";
                break;*/
            case PROCESO_ENVIO:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido que dicho comprobante esta en proceso por la Sunat.";
                break;
            case RECHAZADO:
            case ERROR:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido que dicho comprobante ha sido rechazado o tiene errores reportado" + " por la Sunat.";
                break;
        }

        if (mensajeValidacion != null) {
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        if(tipoDocumento.contains("09")){
            fechaEmision = guiaEntity.getFechaEmision();
        }else{
            System.out.println("tipo de documento 2: "+tipoDocumento);
            fechaEmision = entity.getFechaEmision();
        }
        long fechaEmisionLong = UtilFormat.fechaDate(fechaEmision).getTime();
        long fechaToday = new Date().getTime();
        int diasTranscurridos = (int) TimeUnit.DAYS.convert((fechaToday - fechaEmisionLong), TimeUnit.MILLISECONDS);

        /*if (diasTranscurridos > rangoDiasAceptable) {
            mensajeValidacion = "El comprobante, " + identificadorDocumento + ",tiene mas de " + rangoDiasAceptable + " dias de su fecha de emision(" + fechaEmision + ") y no puede ser anulado.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }*/
        return fechaEmision;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }

}
