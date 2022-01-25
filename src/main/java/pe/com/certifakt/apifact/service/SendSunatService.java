package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.bean.ResponseSunat;
import pe.com.certifakt.apifact.dto.GetStatusCdrDTO;
import pe.com.certifakt.apifact.dto.GetStatusExcelDTO;

public interface SendSunatService {
    ResponseSunat getStatus(String nroTicket, String tipoResumen, String rucEmisor);

    ResponseSunat getStatusCDR(GetStatusCdrDTO statusDto, String rucEmisor);

    ResponseSunat sendSummary(String fileName, String contentFileBase64, String rucEmisor);

    ResponseSunat sendBillPaymentVoucher(String fileName, String contentFileBase64, String rucEmisor);

    ResponseSunat sendBillOtrosCpe(String fileName, String contentFileBase64, String rucEmisor);

    ResponseSunat sendBillGuiaRemision(String fileName, String contentFileBase64, String rucEmisor);



}
