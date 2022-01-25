package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import pe.com.certifakt.apifact.bean.DocumentCpe;
import pe.com.certifakt.apifact.bean.OtherDocumentCpe;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OtherDocumentCpeDeserializer extends FieldsInput<OtherDocumentCpe> {

    @Autowired
    private DocumentosRelacionadosDeserializer detalleDeserializer;

    @Override
    public OtherDocumentCpe deserialize(JsonParser jsonParser,
                                        DeserializationContext context) throws IOException, JsonProcessingException {

        Iterator<JsonNode> iteratorItems;
        List<DocumentCpe> items = null;
        OtherDocumentCpe objectResult;
        DocumentCpe item;
        JsonNode trama;
        JsonNode campoTrama;
        JsonNode itemJson;

        String serie = null;
        Integer numero = null;
        String fechaEmision = null;
        String horaEmision = null;
        String tipoComprobante = null;

        String numeroDocumentoIdentidadReceptor = null;
        String tipoDocumentoIdentidadReceptor = null;
        String nombreComercialReceptor = null;
        String denominacionReceptor = null;

        String ubigeoDomicilioFiscalReceptor = null;
        String direccionCompletaDomicilioFiscalReceptor = null;
        String urbanizacionDomicilioFiscalReceptor = null;
        String departamentoDomicilioFiscalReceptor = null;
        String provinciaDomicilioFiscalReceptor = null;
        String distritoDomicilioFiscalReceptor = null;
        String codigoPaisDomicilioFiscalReceptor = null;
        String emailReceptor = null;

        String regimen = null;
//        BigDecimal tasa = null;
        String observaciones = null;
        BigDecimal importeTotalRetenidoPercibido = null;
        BigDecimal importeTotalPagadoCobrado = null;
        BigDecimal montoRedondeoImporteTotal = null;
        String codigoMoneda = null;

        String mensajeError;
        boolean isRetencion;
        String etiquetaParam;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(tipoComprobanteLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "45[" + tipoComprobanteLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoComprobante = campoTrama.textValue();
                if (!(tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION) ||
                        tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_PERCEPTION))) {
                    mensajeError = "Se espera que el tipo comprobante sea 20 ó 40";
                    throw new DeserializerException(mensajeError);
                }
            }
        }
        isRetencion = (tipoComprobante != null && tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION));
        campoTrama = trama.get(serieLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "46[" + serieLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                serie = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numero = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER
                        + "[" + numeroLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(fechaEmisionLabel);
        fechaEmision = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(horaEmisionLabel);
        horaEmision = (campoTrama != null) ? campoTrama.textValue() : null;

        etiquetaParam = (isRetencion) ? numeroDocumentoIdentidadReceptorRetencionLabel : numeroDocumentoIdentidadReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "47[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numeroDocumentoIdentidadReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? tipoDocumentoIdentidadReceptorRetencionLabel : tipoDocumentoIdentidadReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "48[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoDocumentoIdentidadReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? nombreComercialReceptorRetencionLabel : nombreComercialReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "48[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                nombreComercialReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? denominacionReceptorRetencionLabel : denominacionReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "49[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                denominacionReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? ubigeoDomicilioFiscalReceptorRetencionLabel : ubigeoDomicilioFiscalReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "50["
                        + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                ubigeoDomicilioFiscalReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? direccionCompletaDomicilioFiscalReceptorRetencionLabel : direccionCompletaDomicilioFiscalReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "51[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                direccionCompletaDomicilioFiscalReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? urbanizacionDomicilioFiscalReceptorRetencionLabel : urbanizacionDomicilioFiscalReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "52[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                urbanizacionDomicilioFiscalReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? departamentoDomicilioFiscalReceptorRetencionLabel : departamentoDomicilioFiscalReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "53[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                departamentoDomicilioFiscalReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? provinciaDomicilioFiscalReceptorRetencionLabel : provinciaDomicilioFiscalReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "54[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                provinciaDomicilioFiscalReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? distritoDomicilioFiscalReceptorRetencionLabel : distritoDomicilioFiscalReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "55[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                distritoDomicilioFiscalReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? codigoPaisDomicilioFiscalReceptorRetencionLabel : codigoPaisDomicilioFiscalReceptorPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "56[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                codigoPaisDomicilioFiscalReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = emailReceptorRetencionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "57[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                emailReceptor = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? regimenRetencionLabel : regimenPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "58[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                regimen = campoTrama.textValue();
            }
        }

//        etiquetaParam = (isRetencion) ? tasaRetencionLabel : tasaPercepcionLabel;
//        campoTrama = trama.get(etiquetaParam);
//        if (campoTrama != null && !campoTrama.isNull()) {
//            if (campoTrama.isNumber()) {
//                tasa = campoTrama.decimalValue();
//            } else {
//                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + etiquetaParam + "]";
//                throw new DeserializerException(mensajeError);
//            }
//        }
        campoTrama = trama.get(observacionesOtroCpeLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "58[" + observacionesOtroCpeLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                observaciones = campoTrama.textValue();
            }
        }

        etiquetaParam = (isRetencion) ? importeTotalRetenidoLabel : importeTotalPercibidoLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                importeTotalRetenidoPercibido = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "["
                        + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        etiquetaParam = (isRetencion) ? importeTotalPagadoLabel : importeTotalCobradoLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                importeTotalPagadoCobrado = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "["
                        + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(montoRedondeoImporteTotalLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoRedondeoImporteTotal = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "["
                        + montoRedondeoImporteTotalLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        etiquetaParam = (isRetencion) ? codigoMonedaRetencionLabel : codigoMonedaPercepcionLabel;
        campoTrama = trama.get(etiquetaParam);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING
                        + "59[" + etiquetaParam + "]";
                throw new DeserializerException(mensajeError);
            } else {
                codigoMoneda = campoTrama.textValue();
            }
        }


        etiquetaParam = (isRetencion) ? documentosRelacionadosRetencionLabel : documentosRelacionadosPercepcionLabel;
        if (trama.get(etiquetaParam) != null) {
            items = new ArrayList<DocumentCpe>();
            iteratorItems = trama.get(etiquetaParam).elements();
            while (iteratorItems.hasNext()) {

                itemJson = iteratorItems.next();
                detalleDeserializer.tipoComprobante = tipoComprobante;
                item = detalleDeserializer.deserialize(itemJson.traverse(jsonParser.getCodec()), context);
                items.add(item);
            }
        }

        objectResult = new OtherDocumentCpe();
        objectResult.setTipoComprobante(tipoComprobante);
        objectResult.setSerie(serie);
        objectResult.setNumero(numero);
        objectResult.setFechaEmision(fechaEmision);
        objectResult.setHoraEmision(horaEmision);
        objectResult.setNumeroDocumentoIdentidadReceptor(numeroDocumentoIdentidadReceptor);
        objectResult.setTipoDocumentoIdentidadReceptor(tipoDocumentoIdentidadReceptor);
        objectResult.setNombreComercialReceptor(nombreComercialReceptor);
        objectResult.setDenominacionReceptor(denominacionReceptor);
        objectResult.setUbigeoDomicilioFiscalReceptor(ubigeoDomicilioFiscalReceptor);
        objectResult.setDireccionCompletaDomicilioFiscalReceptor(direccionCompletaDomicilioFiscalReceptor);
        objectResult.setUrbanizacionDomicilioFiscalReceptor(urbanizacionDomicilioFiscalReceptor);
        objectResult.setDepartamentoDomicilioFiscalReceptor(departamentoDomicilioFiscalReceptor);
        objectResult.setProvinciaDomicilioFiscalReceptor(provinciaDomicilioFiscalReceptor);
        objectResult.setDistritoDomicilioFiscalReceptor(distritoDomicilioFiscalReceptor);
        objectResult.setCodigoPaisDomicilioFiscalReceptor(codigoPaisDomicilioFiscalReceptor);
        objectResult.setEmailReceptor(emailReceptor);
        objectResult.setRegimen(regimen);
//        objectResult.setTasa(tasa);
        objectResult.setObservaciones(observaciones);
        objectResult.setImporteTotalRetenidoPercibido(importeTotalRetenidoPercibido);
        objectResult.setImporteTotalPagadoCobrado(importeTotalPagadoCobrado);
        objectResult.setMontoRedondeoImporteTotal(montoRedondeoImporteTotal);
        objectResult.setCodigoMoneda(codigoMoneda);
        objectResult.setDocumentosRelacionados(items);

        return objectResult;
    }

}
