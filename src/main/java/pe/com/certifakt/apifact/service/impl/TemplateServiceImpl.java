package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.exception.SignedException;
import pe.com.certifakt.apifact.exception.TemplateException;
import pe.com.certifakt.apifact.service.CompanyService;
import pe.com.certifakt.apifact.service.TemplateService;
import pe.com.certifakt.apifact.signed.Signed;
import pe.com.certifakt.apifact.template.*;
import pe.com.certifakt.apifact.template21.BoletaTemplate21;
import pe.com.certifakt.apifact.template21.FacturaTemplate21;
import pe.com.certifakt.apifact.template21.NotaCreditoTemplate21;
import pe.com.certifakt.apifact.template21.NotaDebitoTemplate21;
import pe.com.certifakt.apifact.templateose.BoletaTemplateOse;
import pe.com.certifakt.apifact.templateose.FacturaTemplateOse;
import pe.com.certifakt.apifact.templateose.NotaCreditoTemplateOse;
import pe.com.certifakt.apifact.templateose.NotaDebitoTemplateOse;
import pe.com.certifakt.apifact.util.*;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Service
public class TemplateServiceImpl implements TemplateService {

    private final FacturaTemplate invoiceTemplate;
    private final FacturaTemplate21 invoiceTemplate21;
    private final BoletaTemplate boletaTemplate;
    private final BoletaTemplate21 boletaTemplate21;
    private final FacturaTemplateOse invoiceTemplateOse;
    private final BoletaTemplateOse boletaTemplateOse;
    private final NotaCreditoTemplateOse creditNoteTemplateOse;
    private final GuiaRemisionTemplate guiaRemisionTemplate;
    private final NotaCreditoTemplate creditNoteTemplate;
    private final NotaCreditoTemplate21 creditNoteTemplate21;
    private final NotaDebitoTemplateOse debitNoteTemplateOse;
    private final NotaDebitoTemplate debitNoteTemplate;
    private final NotaDebitoTemplate21 debitNoteTemplate21;
    private final SummaryTemplate summaryTemplate;
    private final VoidedDocumentsTemplate voidedDocumentsTemplate;
    private final RetencionTemplate retentionTemplate;
    private final PercepcionTemplate percepcionTemplate;
    private final Signed signed;

    @Autowired
    private CompanyService companyService;

    @Override
    public Map<String, String> buildPaymentVoucherSign(PaymentVoucher voucher)
            throws TemplateException, SignedException {

        String xmlGenerado = null;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;

        switch (voucher.getTipoComprobante()) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = invoiceTemplate.buildInvoice(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = invoiceTemplate21.buildInvoice(voucher);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = boletaTemplate.buildBoleta(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = boletaTemplate21.buildInvoice(voucher);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = creditNoteTemplate.buildCreditNote(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = creditNoteTemplate21.buildCreditNote(voucher);
                }
                break;
            default:
                if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = debitNoteTemplate.buildDebitNote(voucher);
                } else if (voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = debitNoteTemplate21.buildDebitNote(voucher);
                }
                break;
        }

        idSignature = "S" + voucher.getTipoComprobante() + voucher.getSerie() + "-" + voucher.getNumero();
        signatureResp = signed.signBliz(xmlGenerado, idSignature);
        nombreDocumento = voucher.getRucEmisor() + "-" + voucher.getTipoComprobante() + "-" +
                voucher.getSerie() + "-" + voucher.getNumero();

        resp = buildDataTemplate(signatureResp, nombreDocumento);
        resp.put(ConstantesParameter.CODIGO_HASH, UtilArchivo.generarCodigoHash(signatureResp.toString()));
        return resp;
    }

    @Override
    public Map<String, String> buildPaymentVoucherSignOse(PaymentVoucher voucher) throws TemplateException, SignedException {
        String xmlGenerado=null;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;

        switch (voucher.getTipoComprobante()) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = invoiceTemplateOse.buildInvoice(voucher);
                }else if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = invoiceTemplateOse.buildInvoice(voucher);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
                if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = boletaTemplateOse.buildInvoice(voucher);
                }else if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = boletaTemplateOse.buildInvoice(voucher);
                }
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = creditNoteTemplateOse.buildCreditNote(voucher);

                }else if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = creditNoteTemplateOse.buildCreditNote(voucher);
                }
                break;
            default:

                if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    xmlGenerado = debitNoteTemplateOse.buildDebitNote(voucher);
                }else if(voucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    xmlGenerado = debitNoteTemplateOse.buildDebitNote(voucher);
                }
                break;
        }

        idSignature = "S"+voucher.getTipoComprobante()+voucher.getSerie()+"-"+voucher.getNumero();
        signatureResp = signed.sign(xmlGenerado, idSignature);
        nombreDocumento = voucher.getRucEmisor()+"-"+voucher.getTipoComprobante()+"-"+
                voucher.getSerie()+"-"+voucher.getNumero();

        resp = buildDataTemplate(signatureResp, nombreDocumento);
        resp.put(ConstantesParameter.CODIGO_HASH, UtilArchivo.generarCodigoHash(signatureResp.toString()));
        return resp;
    }


    @Override
    public Map<String, String> buildSummaryDailySign(Summary summary) throws TemplateException, SignedException {

        Map<String, String> resp;
        String xml;

        xml = summaryTemplate.buildSummary(summary);

        SignatureResponse response = signed.signBliz(xml, UtilFormat.concat("S-", summary.getSignId()));
        String nameDocument = UtilFormat.concat(summary.getRucEmisor(),
                "-", summary.getId());

        resp = buildDataTemplate(response, nameDocument);

        return resp;
    }
    @Override
    public Map<String, String> buildSummaryDailySignOse(Summary summary) throws TemplateException, SignedException {
        Map<String, String> resp;
        String xml;

        xml = summaryTemplate.buildSummary(summary);

        SignatureResponse response = signed.sign(xml, UtilFormat.concat("S-", summary.getSignId()));
        String nameDocument = UtilFormat.concat(summary.getRucEmisor(),
                "-",summary.getId());

        resp = buildDataTemplate(response, nameDocument);

        return resp;
    }


    @Override
    public Map<String, String> buildVoidedDocumentsSign(Voided voided) throws TemplateException, SignedException {

        String xmlGenerado;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;

        xmlGenerado = voidedDocumentsTemplate.buildVoidedDocuments(voided);
        idSignature = "S" + voided.getId();
        signatureResp = signed.signBliz(xmlGenerado, idSignature);

        nombreDocumento = voided.getRucEmisor() + "-" + voided.getId();
        resp = buildDataTemplate(signatureResp, nombreDocumento);

        return resp;

    }

    @Override
    public Map<String, String> buildOtherDocumentCpeSign(OtherDocumentCpe otherDocumentCpe) throws TemplateException, SignedException {

        String xmlGenerado;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;

        if (otherDocumentCpe.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_RETENTION)) {
            xmlGenerado = retentionTemplate.buildRetention(otherDocumentCpe);
        } else {
            xmlGenerado = percepcionTemplate.buildPerception(otherDocumentCpe);
        }
        idSignature = "S" + otherDocumentCpe.getTipoComprobante() + "-" + otherDocumentCpe.getSerie() + "-" +
                otherDocumentCpe.getNumero();

        signatureResp = signed.sign(xmlGenerado, idSignature);
        nombreDocumento = otherDocumentCpe.getNumeroDocumentoIdentidadEmisor() + "-" +
                otherDocumentCpe.getTipoComprobante() + "-" + otherDocumentCpe.getSerie() + "-" +
                otherDocumentCpe.getNumero();
        resp = buildDataTemplate(signatureResp, nombreDocumento);

        return resp;

    }

    @Override
    public Map<String, String> buildGuiaRemisionSign(GuiaRemision guiaRemision)
            throws TemplateException, SignedException {

        String xmlGenerado;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;

        xmlGenerado = guiaRemisionTemplate.buildGuiaRemision(guiaRemision);
        idSignature = "S" + guiaRemision.getTipoComprobante() + "-" + guiaRemision.getSerie() + "-" +
                guiaRemision.getNumero();
        signatureResp = signed.signBliz(xmlGenerado, idSignature);
        nombreDocumento = guiaRemision.getNumeroDocumentoIdentidadRemitente() + "-" +
                guiaRemision.getTipoComprobante() + "-" + guiaRemision.getSerie() + "-" +
                guiaRemision.getNumero();
        resp = buildDataTemplate(signatureResp, nombreDocumento);

        return resp;
    }

    @Override
    public Map<String, String> buildGuiaRemisionOseSign(GuiaRemision guiaRemision)
            throws TemplateException, SignedException {

        String xmlGenerado;
        String idSignature;
        String nombreDocumento;
        Map<String, String> resp;
        SignatureResponse signatureResp;

        xmlGenerado = guiaRemisionTemplate.buildGuiaRemision(guiaRemision);
        idSignature = "S" + guiaRemision.getTipoComprobante() + "-" + guiaRemision.getSerie() + "-" +
                guiaRemision.getNumero();
        signatureResp = signed.sign(xmlGenerado, idSignature);
        nombreDocumento = guiaRemision.getNumeroDocumentoIdentidadRemitente() + "-" +
                guiaRemision.getTipoComprobante() + "-" + guiaRemision.getSerie() + "-" +
                guiaRemision.getNumero();
        resp = buildDataTemplate(signatureResp, nombreDocumento);

        return resp;
    }

    private Map<String, String> buildDataTemplate(SignatureResponse signatureResp, String nombreDocumento) throws SignedException {

        Map<String, String> resp;
        File zipeado;

        zipeado = UtilArchivo.comprimir(signatureResp.getSignatureFile(),
                ConstantesParameter.TYPE_FILE_XML, nombreDocumento);

        resp = new HashMap<>();
        resp.put(ConstantesParameter.PARAM_NAME_DOCUMENT, nombreDocumento);
        try {

            byte encoded[] = Base64.getEncoder().encode(signatureResp.getSignatureFile().toByteArray());
            String xmlBase64 = new String(encoded);

            resp.put(ConstantesParameter.PARAM_FILE_ZIP_BASE64, UtilConversion.encodeFileToBase64(zipeado));
            resp.put(ConstantesParameter.PARAM_FILE_XML_BASE64, xmlBase64);
        } catch (IOException e) {
            throw new SignedException(e.getMessage());
        }

        return resp;
    }
    /**********************************************************************************************************/
    /*private Map<String, String> buildDataExcel(SignatureResponse signatureResp, String nombreDocumento) throws SignedException {

        Map<String, String> resp;
        File zipeado;

        zipeado = UtilArchivo.comprimirExcel(signatureResp.getSignatureFile(),ConstantesParameter.TYPE_FILE_EXCEL, nombreDocumento);

        resp = new HashMap<>();
        resp.put(ConstantesParameter.PARAM_NAME_DOCUMENT, nombreDocumento);

        //byte encoded[] = Base64.getEncoder().encode(signatureResp.getSignatureFile().toByteArray());
        //String xlsBase64 = new String(encoded);

        //resp.put(ConstantesParameter.PARAM_FILE_ZIP_BASE64, UtilConversion.encodeFileToBase64(zipeado));
        resp.put(ConstantesParameter.PARAM_FILE_EXCEL, xlsBase64);

        return resp;
    }*/
    /**********************************************************************************************************/


}