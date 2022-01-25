package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiaRemisionDeserializer extends FieldsInput<GuiaRemision> {

    @Autowired
    private TramoTrasladoDeserializer tramoTrasladoDeserializer;
    @Autowired
    private GuiaItemDeserializer guiaItemDeserializer;
    @Autowired
    private CampoAdicionalGuiaDeserealizer campoAdicionalGuiaDeserializer;

    @Override
    public GuiaRemision deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {

        GuiaRemision guiaRemision = null;
        String serie = null;
        Integer numero = null;
        String fechaEmision = null;
        String horaEmision = null;
        String serieBaja = null;
        Integer numeroBaja = null;
        String identificadorDocumentoRelacionado = null;
        String numeroDAM = null;
        String numeroManifiestoCarga = null;
        String codigoTipoDocumentoRelacionado = null;
        String numeroDocumentoIdentidadDestinatario = null;
        String tipoDocumentoIdentidadDestinatario = null;
        String denominacionDestinatario = null;
        String numeroDocumentoIdentidadTercero = null;
        String tipoDocumentoIdentidadTercero = null;
        String denominacionTercero = null;
        String motivoTraslado = null;
        String descripcionMotivoTraslado = null;
        Boolean indicadorTransbordoProgramado = null;
        BigDecimal pesoTotalBrutoBienes = null;
        String unidadMedidaPesoBruto = null;
        Long numeroBultos = null;
        String ubigeoPuntoLlegada = null;
        String direccionPuntoLlegada = null;
        String numeroContenedor = null;
        String ubigeoPuntoPartida = null;
        String direccionPuntoPartida = null;
        String codigoPuerto = null;

        BigDecimal totalValorVentaExportacion = null;
        BigDecimal totalValorVentaGravada = null;
        BigDecimal totalValorVentaInafecta = null;
        BigDecimal totalValorVentaExonerada = null;
        BigDecimal totalValorVentaGratuita = null;
        BigDecimal totalValorBaseIsc = null;
        BigDecimal totalValorBaseOtrosTributos = null;
        BigDecimal totalValorVentaGravadaIVAP = null;
        BigDecimal totalImpOperacionGratuita = null;
        BigDecimal totalDescuento = null;
        BigDecimal totalIgv = null;
        BigDecimal totalIsc = null;
        BigDecimal totalOtrostributos = null;
        BigDecimal descuentoGlobales = null;
        BigDecimal sumatoriaOtrosCargos = null;
        BigDecimal importeTotalVenta = null;

        List<TramoTraslado> listTramoTraslado;
        TramoTraslado itemTramoTraslado;
        List<GuiaItem> listGuiaItem;
        List<String> listObservaciones;
        List<CampoAdicionalGuia> camposAdicionales;
        GuiaItem itemGuiaItem;
        CampoAdicionalGuia campoAdicional;
        Iterator<JsonNode> iteratorTramoTraslado;
        Iterator<JsonNode> iteratorGuiaItem;
        Iterator<JsonNode> iteratorObservaciones;
        Iterator<JsonNode> iteratorCamposAdicionales;
        JsonNode campoTrama;
        JsonNode tramoTrasladoJson;
        JsonNode guiaItemJson;
        JsonNode observacionJson;
        JsonNode trama;
        JsonNode campoAdicionalJson;

        String mensajeError = null;
        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(serieGuiaLabel);
        System.out.println("SerieGuiatrama: "+campoTrama);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "26[" + serieGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                serie = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isNumber()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + numeroGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numero = campoTrama.intValue();
            }
        }

        campoTrama = trama.get(fechaEmisionGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "27[" + fechaEmisionGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                fechaEmision = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(horaEmisionLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "28[" + horaEmisionLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                horaEmision = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(serieBajaGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "29[" + serieBajaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                serieBaja = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroBajaGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.canConvertToInt()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + numeroBajaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numeroBaja = campoTrama.intValue();
            }
        }

        camposAdicionales = null;
        if (trama.get(camposAdicionalesGuiaLabel) != null) {

            camposAdicionales = new ArrayList<CampoAdicionalGuia>();
            iteratorCamposAdicionales = trama.get(camposAdicionalesGuiaLabel).elements();
            while (iteratorCamposAdicionales.hasNext()) {

                campoAdicionalJson = iteratorCamposAdicionales.next();
                campoAdicional = campoAdicionalGuiaDeserializer.deserialize(campoAdicionalJson.traverse(jsonParser.getCodec()), context);
                camposAdicionales.add(campoAdicional);
            }
        }

        campoTrama = trama.get(numeracionDAMLabel);
        if (campoTrama != null && !campoTrama.isNull() && campoTrama.textValue() != null) {
            numeroDAM = campoTrama.textValue();
        }

        campoTrama = trama.get(totalValorVentaExportacionlabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaExportacion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaExportacionlabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaGravadaLabel);

        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {

                totalValorVentaGravada = campoTrama.decimalValue();
            } else {
                
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaGravadaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorBaseOtrosTributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorBaseOtrosTributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorBaseOtrosTributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorBaseIscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorBaseIsc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorBaseIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalImpOperGratuitaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalImpOperacionGratuita = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalImpOperGratuitaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaInafectaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaInafecta = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaInafectaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaExoneradaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaExonerada = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaExoneradaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaGratuitaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaGratuita = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaGratuitaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaGravadaIVAPLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaGravadaIVAP = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaGravadaIVAPLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalDescuentoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalDescuento = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalDescuentoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(totalIgvLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalIgv = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalIgvLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalIscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalIsc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalOtrostributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalOtrostributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalOtrostributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(descuentoGlobalesLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                descuentoGlobales = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + descuentoGlobalesLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalOtrosCargosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if(campoTrama.toString().length() > 0){
                if (campoTrama.isNumber()) {
                    if(campoTrama.decimalValue().compareTo(BigDecimal.ZERO) > 0){
                        sumatoriaOtrosCargos = campoTrama.decimalValue();
                    }

                } else {
                    mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER +" "+ campoTrama +"[" + totalOtrosCargosLabel + "]";
                    throw new DeserializerException(mensajeError);
                }
            }

        }
        campoTrama = trama.get(importeTotalLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                importeTotalVenta = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + importeTotalLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(numeracionManifiestoCargaLabel);
        if (campoTrama != null && !campoTrama.isNull() && campoTrama.textValue() != null) {
            numeroManifiestoCarga = campoTrama.textValue();
        }
        campoTrama = trama.get(identificadorDocumentoRelacionadoGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull() && campoTrama.textValue() != null) {
            identificadorDocumentoRelacionado = campoTrama.textValue();
        }

        campoTrama = trama.get(tipoDocumentoRelacionadoGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull() && campoTrama.textValue() != null) {
            codigoTipoDocumentoRelacionado = campoTrama.textValue();
        }

        campoTrama = trama.get(numeroIdentidadDestinatarioGuiaLabel);

        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {

                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "30[" + numeroIdentidadDestinatarioGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {

                numeroDocumentoIdentidadDestinatario = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(tipoDocumentoIdentidadDestinatarioGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "31[" + tipoDocumentoIdentidadDestinatarioGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoDocumentoIdentidadDestinatario = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(denominacionDestinatarioGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "32[" + denominacionDestinatarioGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                denominacionDestinatario = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroDocumentoIdentidadProveedorGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "33[" + numeroDocumentoIdentidadProveedorGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numeroDocumentoIdentidadTercero = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(tipoDocumentoIdentidadProveedorGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "34[" + tipoDocumentoIdentidadProveedorGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoDocumentoIdentidadTercero = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(denominacionProveedorGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "35[" + denominacionProveedorGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                denominacionTercero = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(motivoTrasladoGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "36[" + motivoTrasladoGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                motivoTraslado = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(descripcionMotivoTrasladoGuiaLabel);
        System.out.println("descripcion campotrama: "+campoTrama);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "37[" + descripcionMotivoTrasladoGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                descripcionMotivoTraslado = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(indicadorTransbordoProgramadoGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull() && campoTrama.isBoolean()) {
            indicadorTransbordoProgramado = campoTrama.asBoolean();
        } else {
            mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_BOOLEAN + "[" + indicadorTransbordoProgramadoGuiaLabel + "]";
            throw new DeserializerException(mensajeError);
        }


        campoTrama = trama.get(pesoTotalBrutoBienesGuiaLabel);
        System.out.println("campotramaPesoTotalBruto: "+campoTrama);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                System.out.println("campotramaPesoTotalBrutoBiem: "+campoTrama);
                pesoTotalBrutoBienes = campoTrama.decimalValue();
            } else {
                System.out.println("campotramaPesoTotalBrutoError: "+campoTrama);
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + pesoTotalBrutoBienesGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(unidadMedidaPesoBrutoGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "38[" + unidadMedidaPesoBrutoGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                unidadMedidaPesoBruto = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroBultosGuiaLabel);
        System.out.println("Bultos: "+campoTrama);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isNumber()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "39[" + numeroBultosGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                System.out.println("BultosBiem: "+campoTrama);
                numeroBultos = campoTrama.longValue();
            }
        }

        campoTrama = trama.get(ubigeoPuntoLlegadaGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "40[" + ubigeoPuntoLlegadaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                ubigeoPuntoLlegada = campoTrama.textValue();
            }
        }


        campoTrama = trama.get(direccionPuntoLlegadaGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "41[" + direccionPuntoLlegadaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                direccionPuntoLlegada = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroContenedorGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "42[" + numeroContenedorGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                numeroContenedor = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(ubigeoPuntoPartidaGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "43[" + ubigeoPuntoPartidaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                ubigeoPuntoPartida = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(direccionPuntoPartidaGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "44[" + direccionPuntoPartidaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                direccionPuntoPartida = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(codigoPuertoGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull() && campoTrama.textValue() != null) {
            codigoPuerto = campoTrama.textValue();
        }

        listTramoTraslado = null;
        System.out.println("Tramaguialabel: "+trama.get(tramosGuiaLabel));
        if (campoTrama != null && trama.get(tramosGuiaLabel) != null) {

            listTramoTraslado = new ArrayList<TramoTraslado>();
            iteratorTramoTraslado = trama.get(tramosGuiaLabel).elements();
            while (iteratorTramoTraslado.hasNext()) {

                tramoTrasladoJson = iteratorTramoTraslado.next();
                itemTramoTraslado = tramoTrasladoDeserializer.deserialize(tramoTrasladoJson.traverse(jsonParser.getCodec()), context);
                System.out.println("itemtramotralado: "+itemTramoTraslado);
                listTramoTraslado.add(itemTramoTraslado);
            }
        }

        listGuiaItem = null;
        if (trama.get(itemsGuiaLabel) != null) {

            listGuiaItem = new ArrayList<GuiaItem>();
            iteratorGuiaItem = trama.get(itemsGuiaLabel).elements();
            while (iteratorGuiaItem.hasNext()) {

                guiaItemJson = iteratorGuiaItem.next();
                itemGuiaItem = guiaItemDeserializer.deserialize(guiaItemJson.traverse(jsonParser.getCodec()), context);
                listGuiaItem.add(itemGuiaItem);
            }
        }

        listObservaciones = null;
        if (trama.get(observacionesGuiaLabel) != null) {

            listObservaciones = new ArrayList<String>();
            iteratorObservaciones = trama.get(observacionesGuiaLabel).elements();
            while (iteratorObservaciones.hasNext()) {
                observacionJson = iteratorObservaciones.next();
                listObservaciones.add(observacionJson.asText());
            }
        }


        guiaRemision = new GuiaRemision();
        guiaRemision.setSerie(serie);
        guiaRemision.setNumero(numero);
        guiaRemision.setFechaEmision(fechaEmision);
        guiaRemision.setHoraEmision(horaEmision);
        guiaRemision.setSerieGuiaBaja(serieBaja);
        guiaRemision.setNumeroGuiaBaja(numeroBaja);
        guiaRemision.setNumeracionDAM(numeroDAM);
        guiaRemision.setTotalValorVentaExportacion(totalValorVentaExportacion);
        guiaRemision.setTotalValorVentaGravada(totalValorVentaGravada);
        guiaRemision.setTotalValorVentaInafecta(totalValorVentaInafecta);
        guiaRemision.setTotalValorVentaExonerada(totalValorVentaExonerada);
        guiaRemision.setTotalValorVentaGratuita(totalValorVentaGratuita);
        guiaRemision.setTotalValorBaseIsc(totalValorBaseIsc);
        guiaRemision.setTotalValorBaseOtrosTributos(totalValorBaseOtrosTributos);
        guiaRemision.setTotalValorVentaGravadaIVAP(totalValorVentaGravadaIVAP);
        guiaRemision.setTotalDescuento(totalDescuento);
        guiaRemision.setTotalImpOperGratuita(totalImpOperacionGratuita);
        guiaRemision.setTotalIgv(totalIgv);
        guiaRemision.setTotalIsc(totalIsc);
        guiaRemision.setTotalOtrostributos(totalOtrostributos);
        guiaRemision.setDescuentoGlobales(descuentoGlobales);
        guiaRemision.setSumatoriaOtrosCargos(sumatoriaOtrosCargos);
        guiaRemision.setImporteTotalVenta(importeTotalVenta);
        guiaRemision.setNumeracionManifiestoCarga(numeroManifiestoCarga);
        guiaRemision.setIdentificadorDocumentoRelacionado(identificadorDocumentoRelacionado);
        guiaRemision.setCodigoTipoDocumentoRelacionado(codigoTipoDocumentoRelacionado);
        guiaRemision.setNumeroDocumentoIdentidadDestinatario(numeroDocumentoIdentidadDestinatario);
        guiaRemision.setTipoDocumentoIdentidadDestinatario(tipoDocumentoIdentidadDestinatario);
        guiaRemision.setDenominacionDestinatario(denominacionDestinatario);
        guiaRemision.setNumeroDocumentoIdentidadProveedor(numeroDocumentoIdentidadTercero);
        guiaRemision.setTipoDocumentoIdentidadProveedor(tipoDocumentoIdentidadTercero);
        guiaRemision.setDenominacionProveedor(denominacionTercero);
        guiaRemision.setMotivoTraslado(motivoTraslado);
        guiaRemision.setDescripcionMotivoTraslado(descripcionMotivoTraslado);
        guiaRemision.setIndicadorTransbordoProgramado(indicadorTransbordoProgramado);
        guiaRemision.setPesoTotalBrutoBienes(pesoTotalBrutoBienes);
        guiaRemision.setUnidadMedidaPesoBruto(unidadMedidaPesoBruto);
        guiaRemision.setNumeroBultos(numeroBultos);
        guiaRemision.setUbigeoPuntoLlegada(ubigeoPuntoLlegada);
        guiaRemision.setDireccionPuntoLlegada(direccionPuntoLlegada);
        guiaRemision.setNumeroContenedor(numeroContenedor);
        guiaRemision.setUbigeoPuntoPartida(ubigeoPuntoPartida);
        guiaRemision.setDireccionPuntoPartida(direccionPuntoPartida);
        guiaRemision.setCamposAdicionales(camposAdicionales);
        guiaRemision.setCodigoPuerto(codigoPuerto);
        guiaRemision.setTramosTraslados(listTramoTraslado);
        guiaRemision.setBienesToTransportar(listGuiaItem);
        guiaRemision.setObservaciones(listObservaciones);

        return guiaRemision;

    }

}
