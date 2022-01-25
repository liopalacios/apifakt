package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.exception.SignedException;
import pe.com.certifakt.apifact.exception.TemplateException;

import java.util.Map;

public interface TemplateService {

    public Map<String, String> buildPaymentVoucherSign(PaymentVoucher voucher) throws TemplateException, SignedException;

    public Map<String, String> buildPaymentVoucherSignOse(PaymentVoucher voucher) throws TemplateException, SignedException;

    public Map<String, String> buildSummaryDailySign(Summary summary) throws TemplateException, SignedException;

    public Map<String, String> buildVoidedDocumentsSign(Voided voided) throws TemplateException, SignedException;

    public Map<String, String> buildOtherDocumentCpeSign(OtherDocumentCpe otherDocumentCpe) throws TemplateException, SignedException;

    public Map<String, String> buildGuiaRemisionSign(GuiaRemision guiaRemision) throws TemplateException, SignedException;

    public Map<String, String> buildGuiaRemisionOseSign(GuiaRemision guiaRemision) throws TemplateException, SignedException;

    public Map<String, String> buildSummaryDailySignOse(Summary summary) throws TemplateException, SignedException;



}
