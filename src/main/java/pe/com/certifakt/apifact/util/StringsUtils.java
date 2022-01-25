
package pe.com.certifakt.apifact.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringsUtils {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static String getRespuestaEstadoSunat(String codigoEstadoSunat) {

        if (codigoEstadoSunat == null) return "NO_ENVIADO";
        if (codigoEstadoSunat.equals("ACEPT")) return "ACEPTADO";
        if (codigoEstadoSunat.equals("RECHA")) return "RECHAZADO";
        if (codigoEstadoSunat.equals("ANULA")) return "ANULADO";
        if (codigoEstadoSunat.equals("N_ENV")) return "NO_ENVIADO";

        return "NO_ENVIADO";
    }

    public static boolean validateEmail(String emailStr) {
        if (emailStr == null) return false;
        if ((emailStr.trim()).length() == 0) return false;
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static String getNombreTipoDocumentoReceptor(String tipo) {

        String nombreDocumento;

        switch (tipo) {
            case "0":
                nombreDocumento = "DOC.TRIB.NO.DOM.SIN.RUC";
                break;
            case "1":
                nombreDocumento = "DNI";
                break;
            case "4":
                nombreDocumento = "Carnet Ext.";
                break;
            case "6":
                nombreDocumento = "RUC";
                break;
            case "7":
                nombreDocumento = "Pasaporte";
                break;
            case "A":
                nombreDocumento = "CED. DIPLOMATICA DE IDENTIDAD";
                break;
            default:
                nombreDocumento = "-";
                break;
        }

        return nombreDocumento;
    }

    public static String getNombreTipoComprobante(String tipo) {

        String nombreComprobante;

        switch (tipo) {
            case "01":
                nombreComprobante = "FACTURA ELECTRÓNICA";
                break;
            case "03":  nombreComprobante = "BOLETA DE VENTA ELECTRÓNICA";
                break;
            case "07":
                nombreComprobante = "NOTA DE CRÉDITO ELECTRÓNICA";
                break;
            case "08":
                nombreComprobante = "NOTA DE DÉBITO ELECTRÓNICA";
                break;
            case "09":
                nombreComprobante = "GUÍA DE REMISIÓN REMITENTE";
                break;
            case "20":
                nombreComprobante = "RETENCIÓN";
                break;
            case "40":
                nombreComprobante = "PERCEPCIÓN";
                break;
            case "31":
                nombreComprobante = "GUÍA DE REMISIÓN TRANSPORTISTA";
                break;
            default:
                nombreComprobante = "FACTURA ELECTRÓNICA";
                break;
        }

        return nombreComprobante;
    }

    public static String getNombreTipoComprobanteNoElectro(String tipo) {

        String nombreComprobante;

        switch (tipo) {
            case "01":
                nombreComprobante = "FACTURA";
                break;
            case "03":  nombreComprobante = "BOLETA DE VENTA";
                break;
            case "07":
                nombreComprobante = "NOTA DE CRÉDITO";
                break;
            case "08":
                nombreComprobante = "NOTA DE DÉBITO";
                break;
            case "09":
                nombreComprobante = "GUÍA DE REMISIÓN REMITENTE";
                break;
            case "20":
                nombreComprobante = "RETENCIÓN";
                break;
            case "40":
                nombreComprobante = "PERCEPCIÓN";
                break;
            case "31":
                nombreComprobante = "GUÍA DE REMISIÓN TRANSPORTISTA";
                break;
            default:
                nombreComprobante = "FACTURA";
                break;
        }

        return nombreComprobante;
    }
    public static String getModalidadTraslado(String tipo) {

        String nombreModalidad;

        switch (tipo) {
            case "01":
                nombreModalidad = "PÚBLICO";
                break;
            case "02":
                nombreModalidad = "PRIVADO";
                break;
            default:
                nombreModalidad = "ERROR";
                break;
        }

        return nombreModalidad;
    }


    public static String getMotivoTraslado(String tipo) {

        String nombreMotivo;

        switch (tipo) {
            case "01":
                nombreMotivo = "Venta";
                break;
            case "02":
                nombreMotivo = "Compra";
                break;
            case "04":
                nombreMotivo = "Traslado entre establecimientos de la misma empresa";
                break;
            case "08":
                nombreMotivo = "Importación";
                break;
            case "09":
                nombreMotivo = "Exportación";
                break;
            case "13":
                nombreMotivo = "Otros";
                break;
            case "14":
                nombreMotivo = "Venta sujeta a confirmación del comprador";
                break;
            case "18":
                nombreMotivo = "Traslado emisor itinerante CP";
                break;
            case "19":
                nombreMotivo = "Traslado a zona primaria";
                break;
            default:
                nombreMotivo = "ERROR";
                break;
        }

        return nombreMotivo;
    }


    public static String getNombreTipoComprobanteResumido(String tipo) {

        String nombreComprobante;

        switch (tipo) {
            case "01":
                nombreComprobante = "Fac.";
                break;
            case "03":
                nombreComprobante = "Bol.";
                break;
            case "07":
                nombreComprobante = "Nota cre.";
                break;
            case "08":
                nombreComprobante = "Nota deb.";
                break;
            default:
                nombreComprobante = "Fac.";
                break;
        }

        return nombreComprobante;
    }


    public static String getNombreCortoTipoComprobante(String tipo) {

        String nombreComprobante;

        switch (tipo) {
            case "01":
                nombreComprobante = "Factura";
                break;
            case "03":
                nombreComprobante = "Boleta";
                break;
            case "07":
                nombreComprobante = "Nota de crédito.";
                break;
            case "08":
                nombreComprobante = "Nota de débito.";
                break;
            case "20":
                nombreComprobante = "Retención.";
                break;
            case "40":
                nombreComprobante = "Percepción.";
                break;
            default:
                nombreComprobante = "Fac.";
                break;
        }

        return nombreComprobante;
    }

    public static String getTipoNotaDebito(String tipo) {

        String nombreModalidad;

        switch (tipo) {
            case "01":
                nombreModalidad = "Intereses por mora";
                break;
            case "02":
                nombreModalidad = "Aumento en el valor";
                break;
            case "03":
                nombreModalidad = "Penalidades/ otros conceptos";
                break;
            default:
                nombreModalidad = "ERROR";
                break;
        }

        return nombreModalidad;
    }

    public static String getTipoNotaCredito(String tipo) {

        String nombreModalidad;

        switch (tipo) {
            case "01":
                nombreModalidad = "Anulación de la operación";
                break;
            case "02":
                nombreModalidad = "Anulación por error en el RUC";
                break;
            case "03":
                nombreModalidad = "Corrección por error en la descripción";
                break;
            case "04":
                nombreModalidad = "Descuento global";
                break;
            case "05":
                nombreModalidad = "Descuento por ítem";
                break;
            case "06":
                nombreModalidad = "Devolución total";
                break;
            case "07":
                nombreModalidad = "Devolución por ítem";
                break;
            case "08":
                nombreModalidad = "Bonificación";
                break;
            case "09":
                nombreModalidad = "Disminución en el valor";
                break;
            case "10":
                nombreModalidad = "Otros Conceptos";
                break;
            default:
                nombreModalidad = "ERROR";
                break;
        }


        return nombreModalidad;
    }


}
