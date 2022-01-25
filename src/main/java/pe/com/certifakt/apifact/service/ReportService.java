package pe.com.certifakt.apifact.service;


import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.exception.QRGenerationException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;

public interface ReportService {


    ByteArrayInputStream getPdfComprobanteA4(String ruc, String tipo, String serie, Integer numero) throws QRGenerationException, ParseException;

    ByteArrayInputStream getPdfComprobanteTicket(String ruc, String tipo, String serie, Integer numero) throws QRGenerationException, ParseException;

    ByteArrayInputStream getPdfComprobanteA4Publico(String ruc, String tipo, String serie, Integer numero, String fecha, BigDecimal monto) throws QRGenerationException, ParseException;

    ByteArrayInputStream getPdfComprobanteuid(Long idPaymentVoucher, String uuid, String nameDocument, String tipo) throws QRGenerationException, ParseException;

    ByteArrayInputStream getPdfComprobanteOtherCpeUuid(Long idother, String uuid, String nameDocument) throws QRGenerationException, ParseException;

    ByteArrayInputStream getPdfComprobanteGuia(String ruc, String serie, Integer numero, String tipo)
            throws ServiceException, QRGenerationException, ParseException;

    ByteArrayInputStream getPdfGuiaUuid(Long idGuiaRemision, String uid, String nameDocument, String tipoPdf) throws QRGenerationException, ParseException;


    InputStream getReporteEcxel(String ruc, String filtroDesde, String filtroHasta, String filtroTipoComprobante, String filtroRuc, String filtroSerie, Integer filtroNumero) throws IOException;

    ByteArrayInputStream exportAllData(String ruc) throws Exception;

    ByteArrayInputStream getPdfComprobanteOtherCpe(String ruc, String tipo, String serie, Integer numero) throws QRGenerationException, ParseException;


}