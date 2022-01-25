package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.DocumentCpe;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class DocumentosRelacionadosDeserializer extends FieldsInput<DocumentCpe> {

    String tipoComprobante;

    @Override
    public DocumentCpe deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        DocumentCpe objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        String tipoDocumentoRelacionado = null;
        String serieDocumentoRelacionado = null;
        Integer numeroDocumentoRelacionado = null;
        String fechaEmisionDocumentoRelacionado;
        BigDecimal importeTotalDocumentoRelacionado = null;
        String monedaDocumentoRelacionado = null;

        String fechaPagoCobro;
        String numeroPagoCobro = null;
        BigDecimal importePagoSinRetencionCobro = null;
        String monedaPagoCobro = null;

        BigDecimal importeRetenidoPercibido = null;
        String monedaImporteRetenidoPercibido = null;
        String fechaRetencionPercepcion;
        BigDecimal importeTotalToPagarCobrar = null;
        String monedaImporteTotalToPagarCobrar = null;

        String monedaReferenciaTipoCambio = null;
        String monedaObjetivoTasaCambio = null;
        BigDecimal tipoCambio = null;
        String fechaCambio;
        String etiquetaParam;

        String mensajeError;
        boolean isRetencion;

        isRetencion = tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION);
        trama = jsonParser.getCodec().readTree(jsonParser);


        campoTrama = trama.get(tipoDocumentoRelacionadoOtroCpeLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "12[" + tipoDocumentoRelacionadoOtroCpeLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoDocumentoRelacionado = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(serieDocumentoRelacionadoOtroCpeLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "13[" + serieDocumentoRelacionadoOtroCpeLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                serieDocumentoRelacionado = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(numeroDocumentoRelacionadoOtroCpeLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numeroDocumentoRelacionado = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER
                        + "[" + numeroDocumentoRelacionadoOtroCpeLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(fechaEmisionDocumentoRelacionadoOtroCpeLabel);
        fechaEmisionDocumentoRelacionado = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(importeTotalDocumentoRelacionadoOtroCpeLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                importeTotalDocumentoRelacionado = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER
                        + "[" + importeTotalDocumentoRelacionadoOtroCpeLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(monedaDocumentoRelacionadoOtroCpeLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "14[" + monedaDocumentoRelacionadoOtroCpeLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                monedaDocumentoRelacionado = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? fechaPagoCobroRetencionLabel : fechaPagoCobroPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        fechaPagoCobro = (campoTrama != null) ? campoTrama.textValue() : null;

        etiquetaParam = (isRetencion) ? numeroPagoCobroRetencionLabel : numeroPagoCobroPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "15[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numeroPagoCobro = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? importePagoSinRetencionCobroRetencionLabel : importePagoSinRetencionCobroPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                importePagoSinRetencionCobro = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER
                        + "[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        etiquetaParam = (isRetencion) ? monedaPagoCobroRetencionLabel : monedaPagoCobroPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "16[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                monedaPagoCobro = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? importeRetenidoPercibidoRetencionLabel : importeRetenidoPercibidoPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                importeRetenidoPercibido = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER
                        + "[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        etiquetaParam = (isRetencion) ? monedaImporteRetenidoPercibidoRetencionLabel : monedaImporteRetenidoPercibidoPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "17[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                monedaImporteRetenidoPercibido = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? fechaRetencionPercepcionRetencionLabel : fechaRetencionPercepcionPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        fechaRetencionPercepcion = (campoTrama != null) ? campoTrama.textValue() : null;

        etiquetaParam = (isRetencion) ? importeTotalToPagarCobrarRetencionLabel : importeTotalToPagarCobrarPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                importeTotalToPagarCobrar = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER
                        + "[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        etiquetaParam = (isRetencion) ? monedaImporteTotalToPagarCobrarRetencionLabel : monedaImporteTotalToPagarCobrarPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "18[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                monedaImporteTotalToPagarCobrar = campoTrama.textValue();
            }
        }


        campoTrama = trama.get(monedaReferenciaTipoCambioOtroCpeLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "19[" + monedaReferenciaTipoCambioOtroCpeLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                monedaReferenciaTipoCambio = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(monedaObjetivoTasaCambioOtroCpeLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "20[" + monedaObjetivoTasaCambioOtroCpeLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                monedaObjetivoTasaCambio = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(tipoCambioOtroCpeLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                tipoCambio = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER
                        + "[" + tipoCambioOtroCpeLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(fechaCambioOtroCpeLabel);
        fechaCambio = (campoTrama != null && !campoTrama.isNull()) ? campoTrama.textValue() : null;

        objectResult = new DocumentCpe();
        objectResult.setTipoDocumentoRelacionado(tipoDocumentoRelacionado);
        objectResult.setSerieDocumentoRelacionado(serieDocumentoRelacionado);
        objectResult.setNumeroDocumentoRelacionado(numeroDocumentoRelacionado);
        objectResult.setFechaEmisionDocumentoRelacionado(fechaEmisionDocumentoRelacionado);
        objectResult.setImporteTotalDocumentoRelacionado(importeTotalDocumentoRelacionado);
        objectResult.setMonedaDocumentoRelacionado(monedaDocumentoRelacionado);
        objectResult.setFechaPagoCobro(fechaPagoCobro);
        objectResult.setNumeroPagoCobro(numeroPagoCobro);
        objectResult.setImportePagoSinRetencionCobro(importePagoSinRetencionCobro);
        objectResult.setMonedaPagoCobro(monedaPagoCobro);
        objectResult.setImporteRetenidoPercibido(importeRetenidoPercibido);
        objectResult.setMonedaImporteRetenidoPercibido(monedaImporteRetenidoPercibido);
        objectResult.setFechaRetencionPercepcion(fechaRetencionPercepcion);
        objectResult.setImporteTotalToPagarCobrar(importeTotalToPagarCobrar);
        objectResult.setMonedaImporteTotalToPagarCobrar(monedaImporteTotalToPagarCobrar);
        objectResult.setMonedaReferenciaTipoCambio(monedaReferenciaTipoCambio);
        objectResult.setMonedaObjetivoTasaCambio(monedaObjetivoTasaCambio);
        objectResult.setTipoCambio(tipoCambio);
        objectResult.setFechaCambio(fechaCambio);

        return objectResult;
    }
}
