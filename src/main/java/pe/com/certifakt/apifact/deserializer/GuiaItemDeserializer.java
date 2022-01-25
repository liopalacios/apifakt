package pe.com.certifakt.apifact.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.GuiaItem;
import pe.com.certifakt.apifact.exception.DeserializerException;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class GuiaItemDeserializer extends FieldsInput<GuiaItem> {

    @Override
    public GuiaItem deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        GuiaItem guiaItem = null;
        BigDecimal cantidad = null;
        String unidadMedida = null;
        String descripcion = null;
        String codigoItem = null;
        BigDecimal precioItem = null;

        String codigoTipoAfectacionIGV;
        String codigoTipoCalculoISC;
        String codigoProductoSunat;
        String codigoProductoGS1;
        String codigoDescuento;
        BigDecimal valorReferencialUnitario = null;
        BigDecimal precioVentaUnitario = null;
        BigDecimal valorUnitario = null;
        BigDecimal descuento = null;
        BigDecimal igv = null;
        BigDecimal isc = null;
        BigDecimal ivap = null;
        BigDecimal impuestoVentaGratuita = null;
        BigDecimal otrosTributos = null;

        BigDecimal porcentajeIgv = null;
        BigDecimal porcentajeIvap = null;
        BigDecimal porcentajeIsc = null;
        BigDecimal porcentajeOtrosTributos = null;
        BigDecimal porcentajeTributoVentaGratuita = null;

        BigDecimal montoBaseIgv = null;
        BigDecimal montoBaseIvap = null;
        BigDecimal montoBaseExportacion = null;
        BigDecimal montoBaseExonerado = null;
        BigDecimal montoBaseInafecto = null;
        BigDecimal montoBaseGratuito = null;
        BigDecimal montoBaseIsc = null;

        BigDecimal montoBaseOtrosTributos = null;

        JsonNode trama;
        JsonNode campoTrama;
        String mensajeError;
        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(cantidadGuiaLabel);

        if (!campoTrama.isNull()) {
            if (!campoTrama.isNumber()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + cantidadGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                cantidad = campoTrama.decimalValue();
            }
        }

        campoTrama = trama.get(unidadMedidaGuiaLabel);
        if (!campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "21[" + unidadMedidaGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                unidadMedida = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(descripcionGuiaLabel);
        if (!campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "22[" + descripcionGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                descripcion = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(codigoItemGuiaLabel);

        if (!campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "23[" + codigoItemGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                codigoItem = campoTrama.textValue();

            }
        }

        campoTrama = trama.get(precioItemGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                precioItem = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + precioItemGuiaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(descripcionLabel);
        descripcion = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(codigoProductoSunatLabel);
        codigoProductoSunat = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(codigoProductoGS1Label);
        codigoProductoGS1 = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(valorUnitarioLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                valorUnitario = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + valorUnitarioLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }


        campoTrama = trama.get(descuentoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                descuento = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + descuentoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(codigoDescuentoLabel);
        codigoDescuento = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(precioVentaUnitarioLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                precioVentaUnitario = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + precioVentaUnitarioLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(valorReferencialUnitarioLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                valorReferencialUnitario = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + valorReferencialUnitarioLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(montoBaseIgvLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseIgv = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseIgvLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseIvapLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseIvap = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseIvapLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseExportacionLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseExportacion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseExportacionLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseExoneradoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseExonerado = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseExoneradoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseInafectoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseInafecto = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseInafectoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseGratuitoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseGratuito = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseGratuitoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseIscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseIsc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoBaseOtrosTributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoBaseOtrosTributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoBaseOtrosTributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(igvLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                igv = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + igvLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(ivapLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                ivap = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + ivapLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(impuestoVentaGratuitaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                impuestoVentaGratuita = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + impuestoVentaGratuitaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(otrosTributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                otrosTributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + otrosTributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(porcentajeIgvLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeIgv = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeIgvLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(porcentajeIvapLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeIvap = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeIvapLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(porcentajeIscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeIsc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(porcentajeOtrosTributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeOtrosTributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeOtrosTributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(porcentajeTributoVentaGratuitaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeTributoVentaGratuita = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeTributoVentaGratuitaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(iscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                isc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + iscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(tipoCalculoISCLabel);
        codigoTipoCalculoISC = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(tipoAfectacionIGVLabel);
        codigoTipoAfectacionIGV = (campoTrama != null) ? campoTrama.textValue() : null;


        guiaItem = new GuiaItem();
        guiaItem.setCantidad(cantidad);
        guiaItem.setUnidadMedida(unidadMedida);
        guiaItem.setDescripcion(descripcion);
        guiaItem.setCodigoItem(codigoItem);
        guiaItem.setPrecioItem(precioItem);

        guiaItem.setCantidad(cantidad);
        guiaItem.setDescripcion(descripcion);
        guiaItem.setCodigoProductoSunat(codigoProductoSunat);
        guiaItem.setCodigoProductoGS1(codigoProductoGS1);
        guiaItem.setValorUnitario(valorUnitario);
        guiaItem.setDescuento(descuento);
        guiaItem.setCodigoDescuento(codigoDescuento);
        guiaItem.setPrecioVentaUnitario(precioVentaUnitario);
        guiaItem.setValorReferencialUnitario(valorReferencialUnitario);
        guiaItem.setMontoBaseExonerado(montoBaseExonerado);
        guiaItem.setMontoBaseExportacion(montoBaseExportacion);
        guiaItem.setMontoBaseGratuito(montoBaseGratuito);
        guiaItem.setMontoBaseIgv(montoBaseIgv);
        guiaItem.setMontoBaseInafecto(montoBaseInafecto);
        guiaItem.setMontoBaseIsc(montoBaseIsc);
        guiaItem.setMontoBaseIvap(montoBaseIvap);
        guiaItem.setMontoBaseOtrosTributos(montoBaseOtrosTributos);
        guiaItem.setIgv(igv);
        guiaItem.setIsc(isc);
        guiaItem.setIvap(ivap);
        guiaItem.setImpuestoVentaGratuita(impuestoVentaGratuita);
        guiaItem.setOtrosTributos(otrosTributos);
        guiaItem.setPorcentajeIgv(porcentajeIgv);
        guiaItem.setPorcentajeIsc(porcentajeIsc);
        guiaItem.setPorcentajeIvap(porcentajeIvap);
        guiaItem.setPorcentajeOtrosTributos(porcentajeOtrosTributos);
        guiaItem.setPorcentajeTributoVentaGratuita(porcentajeTributoVentaGratuita);
        guiaItem.setCodigoTipoAfectacionIGV(codigoTipoAfectacionIGV);
        guiaItem.setCodigoTipoCalculoISC(codigoTipoCalculoISC);

        return guiaItem;
    }
}
