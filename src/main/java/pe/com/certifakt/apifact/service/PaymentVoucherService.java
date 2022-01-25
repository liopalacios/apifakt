package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface PaymentVoucherService {

    public Map<String, Object> generationPaymentVoucher(PaymentVoucher voucher, Boolean isEdit, UserPrincipal userName) throws ServiceException;

    public Map<String, Object> resendPaymentVoucher(Long idPaymentVoucher, UserPrincipal userName) throws ServiceException;

    public Map<String, Object> getSummaryDocumentsByFechaEmision(String fechaEmision, String rucEmisor, IdentificadorComprobante comprobante) throws ServiceException;

    public Map<String, Object> getSummaryNotaCreditoDocumentsByFechaEmision(String fechaEmision, String rucEmisor, IdentificadorComprobante comprobante) throws ServiceException;

    public ResponsePSE getDocuments(PaymentVoucherParamsInput params);

    public ResponsePSE getDocumentsByIdentificadores(List<Comprobante> datosIn, String rucEmisor);


    Long getPaymentVoucherIdFromTemp(Long idTemporal);

    PaymentVoucherEntity prepareComprobanteForEnvioSunat(String ruc, String tipo, String serie, Integer numero) throws ServiceException;

    //void actualizarEstadoComprobante(PaymentVoucherEntity paymentVoucherEntity);

    ResponsePSE consultaCdrComprobante(String authorization, String ruc, String tipo, String serie, Integer numero);


    List<PaymentVoucherInterDto> getFacturasNoEnviadasCon7Dias();


    List<Map<String, Object>> resendPaymentVoucherBySummary(Long idSummary, UserPrincipal user);

    Map<String,List<EmailSexDaysDetails>> getLisVoucherSeven();

    Map<String,List<EmailSexDaysDetails>> getLisVoucherSix();

    Map<String,List<EmailSexDaysDetails>> getLisVoucherFive();

    Map<String,List<EmailSexDaysDetails>> getLisVoucherFour();

    Map<String,List<EmailSexDaysDetails>> getLisVoucherThree();

    Map<String,Object> resendPaymentVoucherOnlyxml(Long idPayment, UserPrincipal user);

    Map<String,String> postVoucherSeven();

    Map<String,String> postVoucherSix();

    Map<String,String> postVoucherFive();

    Map<String,String> postVoucherFour();

    Map<String,String> postVoucherThree();

    void sendEmailReportSevenDay(Map<String,String> stringMap);

    void sendEmailReportSixDay(Map<String,String> stringMap);

    void sendEmailReportFiveDay(Map<String,String> stringMap);

    void sendEmailReportFourDay(Map<String,String> stringMap);

    void sendEmailReportThreeDay(Map<String,String> stringMap);

    void getPaymentMercadoPago() throws URISyntaxException;

    void getPaymentsPaypal() throws URISyntaxException;

    void generatePaymentsPaypal();

    void generatePaymentMercadoPago() throws ParseException;

    List<String> getFechasPendientesNotas();

    List<String> getRucsPendientesNotas();
}