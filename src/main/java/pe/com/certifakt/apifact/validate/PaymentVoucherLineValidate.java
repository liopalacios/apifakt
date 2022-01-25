package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.PaymentVoucherLine;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.FieldsInput;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class PaymentVoucherLineValidate extends FieldsInput<Object> {

    public void validatePaymentVoucherLine(PaymentVoucherLine item, String tipoComprobante, String ublVersion) throws ValidatorFieldsException {

        boolean existeOperacionGratuita;

        validateUnidadMedida(item.getCodigoUnidadMedida(), tipoComprobante);
        validateCantidad(item.getCantidad(), tipoComprobante, item.getCodigoUnidadMedida());
        validateCodigoProducto(item.getCodigoProducto());
        validateCodigoProductoSunat(item.getCodigoProductoSunat());
        validateDescripcion(item.getDescripcion(), tipoComprobante, ublVersion);
        validateValorUnitario(item.getValorUnitario(), tipoComprobante);
        validateValorVenta(item.getValorVenta(), tipoComprobante);
        if (ublVersion.equals(ConstantesSunat.UBL_VERSION_2_1)) {

            validateDescuento(item.getDescuento(), item.getCodigoDescuento());
//			existeOperacionGratuita = validateOperacionGratuita(item.getMontoBaseGratuito(), item.getImpuestoVentaGratuita(), 
//					item.getPorcentajeTributoVentaGratuita(), item.getValorReferencialUnitario());
            validateOperacionGravada(item.getMontoBaseIgv(), item.getIgv(), item.getPorcentajeIgv(), item.getCodigoTipoAfectacionIGV());
//			validateOperacionISC(item.getMontoBaseIsc(), item.getIsc(), item.getPorcentajeIsc(), item.getCodigoTipoCalculoISC());
//			validateOperacionOtrosTributos(item.getMontoBaseOtrosTributos(), item.getOtrosTributos(), item.getPorcentajeOtrosTributos());
//			if(existeOperacionGratuita) {
//				item.setValorUnitario(BigDecimal.ZERO);
//				item.setPrecioVentaUnitario(null);
//			}
        } else {
//			validatePrecioVentaUnitario(item.getPrecioVentaUnitario(), tipoComprobante);
            validateAfectacionIGV(item.getCodigoTipoAfectacionIGV(), item.getIgv());
//			validateIGV(item.getIgv(), tipoComprobante);
        }

        item.setCodigoUnidadMedida(StringUtils.trimToNull(item.getCodigoUnidadMedida()));
        item.setCodigoProducto(StringUtils.trimToNull(item.getCodigoProducto()));
        item.setCodigoProductoSunat(StringUtils.trimToNull(item.getCodigoProductoSunat()));
        item.setDescripcion(StringUtils.trimToNull(item.getDescripcion()));
        item.setCodigoTipoAfectacionIGV(StringUtils.trimToNull(item.getCodigoTipoAfectacionIGV()));
        item.setCodigoTipoCalculoISC(StringUtils.trimToNull(item.getCodigoTipoCalculoISC()));
		/*
		switch(tipoComprobante) {
			case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
			case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
				break;
			case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
				item.setDescuento(null);
				break;
			default:
				item.setDescuento(null);
				item.setValorReferencialUnitario(null);
		}
		*/
    }

    private void validateUnidadMedida(String unidadMedida, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {

            if (StringUtils.isBlank(unidadMedida)) {
                mensajeValidacion = "El campo [" + codigoUnidadMedidaLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
        if (StringUtils.isNotBlank(unidadMedida)) {

            if (!StringUtils.isAlphanumeric(unidadMedida)) {
                mensajeValidacion = "El campo [" + codigoUnidadMedidaLabel + "] debe ser alfanumerico.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(unidadMedida) > 3) {
                mensajeValidacion = "El campo [" + codigoUnidadMedidaLabel + "] debe tener un maximo de 3 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateCantidad(BigDecimal cantidad, String tipoComprobante, String unidadMedida) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {

            if (cantidad == null) {
                mensajeValidacion = "El campo [" + cantidadLabel + "] es obligatorio";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
        if (cantidad != null) {

            if (cantidad.equals(BigDecimal.ZERO)) {
                mensajeValidacion = "El campo [" + mensajeValidacion + "] es debe ser diferente de cero.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.isBlank(unidadMedida)) {
                mensajeValidacion = "El campo [" + codigoUnidadMedidaLabel + "] es obligatorio, cuando "
                        + "ingresa un valor en el campo [" + mensajeValidacion + "]";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateDescripcion(String descripcion, String tipoComprobante, String ublVersion) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {

            if (StringUtils.isBlank(descripcion)) {
                mensajeValidacion = "El campo [" + descripcionLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
        if (StringUtils.isNotBlank(descripcion)) {
            if (ublVersion.equals(ConstantesSunat.UBL_VERSION_2_1)) {
                if (StringUtils.length(descripcion) > 500) {
                    mensajeValidacion = "El campo [" + descripcionLabel + "] debe tener un maximo de 500 caracteres.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
            } else {
                if (StringUtils.length(descripcion) > 250) {
                    mensajeValidacion = "El campo [" + descripcionLabel + "] debe tener un maximo de 250 caracteres.";
                    throw new ValidatorFieldsException(mensajeValidacion);
                }
            }
        }
    }

    private void validateCodigoProducto(String codigoProducto) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isNotBlank(codigoProducto)) {

            if (StringUtils.length(codigoProducto) > 30) {
                mensajeValidacion = "El campo [" + codigoProductoLabel + "] debe tener un maximo de 30 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateCodigoProductoSunat(String codigoProductoSunat) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isNotBlank(codigoProductoSunat)) {

            if (StringUtils.length(codigoProductoSunat) > 20) {
                mensajeValidacion = "El campo [" + codigoProductoSunatLabel + "] debe tener un maximo de 20 caracteres.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateValorUnitario(BigDecimal valorUnitario, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {

            if (valorUnitario == null) {
                mensajeValidacion = "El campo [" + valorUnitarioLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validatePrecioVentaUnitario(BigDecimal precioVentaUnitario, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {

            if (precioVentaUnitario == null) {
                mensajeValidacion = "El campo [" + precioVentaUnitarioLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    protected boolean validateOperacionGratuita(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje, BigDecimal valorReferencial) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean existeItemGratuita = false;

        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                mensajeValidacion = "El campo [" + montoBaseGratuitoLabel + "] es obligatorio, al ingresar: " +
                        impuestoVentaGratuitaLabel + " o " + porcentajeTributoVentaGratuitaLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (tributo == null) {
                mensajeValidacion = "El campo [" + impuestoVentaGratuitaLabel + "] es obligatorio, al ingresar: " +
                        montoBaseGratuitoLabel + " o " + porcentajeTributoVentaGratuitaLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (porcentaje == null) {
                mensajeValidacion = "El campo [" + porcentajeTributoVentaGratuitaLabel + "] es obligatorio, al ingresar: " +
                        montoBaseGratuitoLabel + " o " + impuestoVentaGratuitaLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }

            existeItemGratuita = true;
        }
        if (valorReferencial == null && existeItemGratuita) {
            mensajeValidacion = "El campo [" + valorReferencialUnitarioLabel + "] es obligatorio, al ingresar una operación gratuita.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

        return existeItemGratuita;
    }

    protected boolean validateOperacionGravada(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje, String codigoAfectacion) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean existeOperacionGravada = false;

        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                mensajeValidacion = "El campo [" + montoBaseIgvLabel + "] es obligatorio, al ingresar: " +
                        igvLabel + " o " + porcentajeIgvLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (tributo == null) {
                mensajeValidacion = "El campo [" + igvLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIgvLabel + " o " + porcentajeIgvLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (porcentaje == null) {
                mensajeValidacion = "El campo [" + porcentajeIgvLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIgvLabel + " o " + igvLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            existeOperacionGravada = true;
        }
        if (existeOperacionGravada) {
            validateAfectacionIGV(codigoAfectacion, tributo);
        }

        return existeOperacionGravada;
    }

    protected boolean validateOperacionISC(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje, String tipoISC) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean existeOperacionIsc = false;
        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                mensajeValidacion = "El campo [" + montoBaseIscLabel + "] es obligatorio, al ingresar: " +
                        iscLabel + " o " + porcentajeIscLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (tributo == null) {
                mensajeValidacion = "El campo [" + iscLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIscLabel + " o " + porcentajeIscLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (porcentaje == null) {
                mensajeValidacion = "El campo [" + porcentajeIscLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIscLabel + " o " + iscLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            existeOperacionIsc = true;
        }
        if (existeOperacionIsc) {
            validateTipoISC(tipoISC);
        }

        return existeOperacionIsc;
    }

    protected boolean validateOperacionOtrosTributos(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        boolean operacionExiste = false;
        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                mensajeValidacion = "El campo [" + montoBaseOtrosTributosLabel + "] es obligatorio, al ingresar: " +
                        otrosTributosLabel + " o " + porcentajeOtrosTributosLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (tributo == null) {
                mensajeValidacion = "El campo [" + otrosTributosLabel + "] es obligatorio, al ingresar: " +
                        montoBaseOtrosTributosLabel + " o " + porcentajeOtrosTributosLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (porcentaje == null) {
                mensajeValidacion = "El campo [" + porcentajeOtrosTributosLabel + "] es obligatorio, al ingresar: " +
                        montoBaseOtrosTributosLabel + " o " + otrosTributosLabel;
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            operacionExiste = true;
        }

        return operacionExiste;
    }

    private void validateValorVenta(BigDecimal valorVenta, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {

            if (valorVenta == null) {
                mensajeValidacion = "El campo [" + valorVentaLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateIGV(BigDecimal igv, String tipoComprobante) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {

            if (igv == null) {
                mensajeValidacion = "El campo [" + igvLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateDescuento(BigDecimal descuento, String tipoDescuento) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (descuento != null && (descuento.compareTo(BigDecimal.ZERO) > 0)) {
            if (StringUtils.isBlank(tipoDescuento)) {
                mensajeValidacion = "El campo [" + codigoDescuentoLabel + "] es obligatorio, al ingresar un descuento.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (!StringUtils.isNumeric(tipoDescuento)) {
                mensajeValidacion = "El campo [" + codigoDescuentoLabel + "] debe contener caracteres numericos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(tipoDescuento) != 2) {
                mensajeValidacion = "El campo [" + codigoDescuentoLabel + "] debe contener solo 2 caracteres numericos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateAfectacionIGV(String tipoIGV, BigDecimal igv) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (igv != null) {
            if (StringUtils.isBlank(tipoIGV)) {
                mensajeValidacion = "El campo [" + tipoAfectacionIGVLabel + "] es obligatorio.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (!StringUtils.isNumeric(tipoIGV)) {
                mensajeValidacion = "El campo [" + tipoAfectacionIGVLabel + "] debe contener caracteres numericos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
            if (StringUtils.length(tipoIGV) != 2) {
                mensajeValidacion = "El campo [" + tipoAfectacionIGVLabel + "] debe contener solo 2 caracteres numericos.";
                throw new ValidatorFieldsException(mensajeValidacion);
            }
        }
    }

    private void validateTipoISC(String tipoISC) throws ValidatorFieldsException {

        String mensajeValidacion = null;

        if (StringUtils.isBlank(tipoISC)) {
            mensajeValidacion = "El campo [" + tipoCalculoISCLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (!StringUtils.isNumeric(tipoISC)) {
            mensajeValidacion = "El campo [" + tipoCalculoISCLabel + "] debe contener caracteres numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (StringUtils.length(tipoISC) != 2) {
            mensajeValidacion = "El campo [" + tipoCalculoISCLabel + "] debe contener solo 2 caracteres numericos.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }

    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }
}
