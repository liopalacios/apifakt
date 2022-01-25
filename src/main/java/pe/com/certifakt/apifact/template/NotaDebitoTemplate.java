package pe.com.certifakt.apifact.template;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.exception.TemplateException;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pe.com.certifakt.apifact.util.UtilXML.appendChild;
import static pe.com.certifakt.apifact.util.UtilXML.formatXML;

@Component
public class NotaDebitoTemplate extends TemplateSunat {

    public String buildDebitNote(PaymentVoucher debitNote) throws TemplateException {

        Signature signature = buildSignature(debitNote);
        String templateBuilt = buildTemplateInXML(debitNote, signature);

        return templateBuilt;
    }

    protected Signature buildSignature(PaymentVoucher debitNote) {

        Signature signature = new Signature();

        signature.setId("IDSignST");
        signature.setUri("#SignST");
        signature.setDenominacionEmisor(debitNote.getDenominacionEmisor());
        signature.setRucEmisor(debitNote.getRucEmisor());

        return signature;
    }

    private String buildTemplateInXML(PaymentVoucher debitNote, Signature signature) throws TemplateException {

        String stringXMLGenerate = null;
        Document document;
        Element elementRoot;

        try {

            document = createDocument();
            elementRoot = addElementRoot(document, ROOT_DEBIT_NOTE, getAttributesElementRoot());

            Element extUBLExtensions = appendChild(document, elementRoot, "ext:UBLExtensions");
            Element extUBLExtension = appendChild(document, extUBLExtensions, "ext:UBLExtension");
            Element extExtensionContent = appendChild(document, extUBLExtension, "ext:ExtensionContent");
            Element sacAdditionalInformation = appendChild(document, extExtensionContent,
                    "sac:AdditionalInformation");

            if (debitNote.getTotalValorVentaGravada() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(document, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(document, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_GRAVADA);
                appendChild(document, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(debitNote.getTotalValorVentaGravada())).
                        setAttribute("currencyID", debitNote.getCodigoMoneda());
            }
            if (debitNote.getTotalValorVentaInafecta() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(document, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(document, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_INAFECTA);
                appendChild(document, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(debitNote.getTotalValorVentaInafecta())).
                        setAttribute("currencyID", debitNote.getCodigoMoneda());
            }
            if (debitNote.getTotalValorVentaExonerada() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(document, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(document, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_EXONERADA);
                appendChild(document, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(debitNote.getTotalValorVentaExonerada())).
                        setAttribute("currencyID", debitNote.getCodigoMoneda());
            }

            appendChild(document, elementRoot, "cbc:UBLVersionID",
                    ConstantesSunat.UBL_VERSION_ID_DEBIT_NOTE);
            appendChild(document, elementRoot, "cbc:CustomizationID",
                    ConstantesSunat.CUSTOMIZATION_ID_DEDIT_NOTE);
            appendChild(document, elementRoot, "cbc:ID", debitNote.getSerie() + "-" + debitNote.getNumero());
            appendChild(document, elementRoot, "cbc:IssueDate", debitNote.getFechaEmision());
            appendChild(document, elementRoot, "cbc:IssueTime", debitNote.getHoraEmision());
            appendChild(document, elementRoot, "cbc:DocumentCurrencyCode", debitNote.getCodigoMoneda());

            appendChildDiscrepancyResponse(document, elementRoot,
                    debitNote.getSerieAfectado() + "-" + debitNote.getNumeroAfectado(),
                    debitNote.getCodigoTipoNotaDebito(),
                    debitNote.getMotivoNota());
            appendChildBillingReference(document, elementRoot,
                    debitNote.getSerieAfectado() + "-" + debitNote.getNumeroAfectado(),
                    debitNote.getTipoComprobanteAfectado());


            if (debitNote.getGuiasRelacionadas() != null && !debitNote.getGuiasRelacionadas().isEmpty()) {
                for (GuiaRelacionada guiaRelacionada : debitNote.getGuiasRelacionadas()) {
                    appendChilDespatchDocumentReference(document, elementRoot,
                            guiaRelacionada.getSerieNumeroGuia(),
                            guiaRelacionada.getCodigoTipoGuia());
                }
            }

            appendChilAdditionalDocumentReference(document, elementRoot,
                    debitNote.getSerieNumeroOtroDocumentoRelacionado(),
                    debitNote.getCodigoTipoOtroDocumentoRelacionado());

            appendChildSignature(document, elementRoot, signature);

            appendChildAccountingSupplierParty(document, elementRoot,
                    debitNote.getRucEmisor(), debitNote.getTipoDocumentoEmisor(),
                    debitNote.getDenominacionEmisor(), debitNote.getNombreComercialEmisor(),
                    debitNote.getCodigoLocalAnexoEmisor());
            appendChildAccountingCustomerParty(document, elementRoot,
                    debitNote.getNumeroDocumentoReceptor(), debitNote.getTipoDocumentoReceptor(),
                    debitNote.getDenominacionReceptor());

            appendChildSumatoria(document, elementRoot, debitNote.getTotalIgv(),
                    ConstantesSunat.CODIGO_TRIBUTO_IGV, debitNote.getCodigoMoneda());
            appendChildSumatoria(document, elementRoot, debitNote.getTotalIsc(),
                    ConstantesSunat.CODIGO_TRIBUTO_ISC, debitNote.getCodigoMoneda());
            appendChildSumatoria(document, elementRoot, debitNote.getTotalOtrostributos(),
                    ConstantesSunat.CODIGO_TRIBUTO_OTROS, debitNote.getCodigoMoneda());

            Element legalMonetaryTotalElement = appendChild(document, elementRoot, "cac:RequestedMonetaryTotal");
            if (debitNote.getSumatoriaOtrosCargos() != null) {
                appendChild(document, legalMonetaryTotalElement, "cbc:ChargeTotalAmount",
                        UtilFormat.format(debitNote.getSumatoriaOtrosCargos())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, debitNote.getCodigoMoneda());
            }
            appendChild(document, legalMonetaryTotalElement, "cbc:PayableAmount",
                    UtilFormat.format(debitNote.getImporteTotalVenta())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, debitNote.getCodigoMoneda());

            appendDebitNoteLine(document, elementRoot,
                    debitNote.getItems(),
                    debitNote.getCodigoMoneda());

            String stringXML = buildStringFromDOM(document);
            stringXMLGenerate = formatXML(stringXML);

        } catch (Exception e) {
            e.printStackTrace();
            throw new TemplateException("Error al generar plantilla para Nota de Credito:" + e.getMessage());
        }

        return stringXMLGenerate;
    }


    private void appendDebitNoteLine(Document document, Element elementRoot,
                                     List<PaymentVoucherLine> debitNotelines, String codigoMoneda) throws TemplateException {

        int correlativoItem = 1;

        for (PaymentVoucherLine item : debitNotelines) {

            Element debitNoteLineElement = appendChild(document, elementRoot, "cac:DebitNoteLine");
            appendChild(document, debitNoteLineElement, "cbc:ID", correlativoItem);

            if (item.getCantidad() != null && item.getCodigoUnidadMedida() != null) {
                appendChild(document, debitNoteLineElement, "cbc:DebitedQuantity", UtilFormat.format(item.getCantidad())).
                        setAttribute("unitCode", item.getCodigoUnidadMedida());
            }

            appendChild(document, debitNoteLineElement, "cbc:LineExtensionAmount", UtilFormat.format(item.getValorVenta())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            if (item.getPrecioVentaUnitario() != null) {

                Element pricingReferenceElement = appendChild(document, debitNoteLineElement,
                        "cac:PricingReference");
                appendChildPricingReference(document, pricingReferenceElement, item.getPrecioVentaUnitario(),
                        ConstantesSunat.CODIGO_TIPO_PRECIO_PRECIO_UNITARIO, codigoMoneda);
            }

            addElementsChildTaxTotal(document, debitNoteLineElement, item.getIgv(),
                    ConstantesSunat.CODIGO_TRIBUTO_IGV, item.getCodigoTipoAfectacionIGV(), codigoMoneda);
            addElementsChildTaxTotal(document, debitNoteLineElement, item.getIsc(),
                    ConstantesSunat.CODIGO_TRIBUTO_ISC, item.getCodigoTipoCalculoISC(), codigoMoneda);

            appendChildItem(document, debitNoteLineElement, item.getDescripcion(), item.getCodigoProducto(),
                    item.getCodigoProductoSunat());

            Element cbcPrice = appendChild(document, debitNoteLineElement, "cac:Price");
            appendChild(document, cbcPrice, "cbc:PriceAmount", UtilFormat.format(item.getValorUnitario())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            item.setNumeroItem(correlativoItem);
            correlativoItem++;
        }
    }

    private void appendChildAccountingCustomerParty(Document document, Element elementRoot,
                                                    String numeroDocumentoReceptor, String tipoDocumentoReceptor, String denominacionReceptor) {

        Element accountingCustomerPartyElement = appendChild(document, elementRoot,
                "cac:AccountingCustomerParty");
        appendChild(document, accountingCustomerPartyElement, "cbc:CustomerAssignedAccountID",
                numeroDocumentoReceptor);
        appendChild(document, accountingCustomerPartyElement, "cbc:AdditionalAccountID",
                tipoDocumentoReceptor);

        Element partyElement = appendChild(document, accountingCustomerPartyElement, "cac:Party");
        Element partyLegalElement = appendChild(document, partyElement, "cac:PartyLegalEntity");
        appendChild(document, partyLegalElement, "cbc:RegistrationName", denominacionReceptor);
    }

    private static void appendChildAccountingSupplierParty(Document document, Element elementRoot,
                                                           String numeroDocumentoEmisor, String tipoDocumentoEmisor, String denominacionEmisor,
                                                           String nombreComercialEmisor, String codigoLocalAnexo) {
        Map<String, String> attributes = new HashMap<>();
        Element accountingSupplierPartyElement = appendChild(document, elementRoot,
                "cac:AccountingSupplierParty");
        appendChild(document, accountingSupplierPartyElement, "cbc:CustomerAssignedAccountID",
                numeroDocumentoEmisor);
        appendChild(document, accountingSupplierPartyElement, "cbc:AdditionalAccountID",
                tipoDocumentoEmisor);

        Element partyElement = appendChild(document, accountingSupplierPartyElement, "cac:Party");
        if (StringUtils.isNotBlank(nombreComercialEmisor)) {
            Element partyNameElement = appendChild(document, partyElement, "cac:PartyName");
            appendChild(document, partyNameElement, "cbc:Name", nombreComercialEmisor);
        }
        attributes.clear();
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_AGENCY_NAME, "PE:SUNAT");
        attributes.put(ConstantesSunat.ATTRIBUTE_LIST_NAME, "Establecimientos anexos");
        //if (StringUtils.isNotBlank(codigoLocalAnexo)) {
            Element postalAddressElement = appendChild(document, partyElement, "cac:PostalAddress");
            appendChild(document, postalAddressElement, "cbc:AddressTypeCode", "0000",attributes);
       // }

        Element partyLegalElement = appendChild(document, partyElement, "cac:PartyLegalEntity");
        appendChild(document, partyLegalElement, "cbc:RegistrationName", denominacionEmisor);

    }

    private void appendChildBillingReference(Document document, Element elementRoot,
                                             String identificadorDocumentoAfectado, String tipoComprobanteAfectado) {

        Element billingReferenceElement = appendChild(document, elementRoot, "cac:BillingReference");

        Element invoiceDocumentReferenceElement = appendChild(document, billingReferenceElement, "cac:InvoiceDocumentReference");
        appendChild(document, invoiceDocumentReferenceElement, "cbc:ID", identificadorDocumentoAfectado);
        appendChild(document, invoiceDocumentReferenceElement, "cbc:DocumentTypeCode", tipoComprobanteAfectado);

    }

    private void appendChildDiscrepancyResponse(Document document, Element elementRoot,
                                                String identificadorDocumentoAfectado, String tipoNotaDebito, String motivo) {

        Element discrepancyResponseElement = appendChild(document, elementRoot, "cac:DiscrepancyResponse");

        appendChild(document, discrepancyResponseElement, "cbc:ReferenceID", identificadorDocumentoAfectado);
        appendChild(document, discrepancyResponseElement, "cbc:ResponseCode", tipoNotaDebito);
        appendChild(document, discrepancyResponseElement, "cbc:Description", motivo);

    }

    private void appendChilDespatchDocumentReference(Document document, Element elementRoot,
                                                     String numeroGuiaRemision, String tipoGuiaRemision) {

        if (StringUtils.isNotBlank(tipoGuiaRemision) && StringUtils.isNotBlank(numeroGuiaRemision)) {

            Element despatchDocumentReferenceElement = appendChild(document, elementRoot, "cac:DespatchDocumentReference");
            appendChildDocumentReference(document, despatchDocumentReferenceElement,
                    numeroGuiaRemision, tipoGuiaRemision);
        }
    }

    private void appendChilAdditionalDocumentReference(Document document, Element elementRoot,
                                                       String numeroDocumentoAdicional, String tipoDocumentoAdicional) {

        if (StringUtils.isNotBlank(tipoDocumentoAdicional) && StringUtils.isNotBlank(numeroDocumentoAdicional)) {

            Element additionalDocumentReferenceElement = appendChild(document, elementRoot, "cac:AdditionalDocumentReference");
            appendChildDocumentReference(document, additionalDocumentReferenceElement,
                    numeroDocumentoAdicional, tipoDocumentoAdicional);
        }
    }

    private void appendChildSumatoria(Document document, Element elementRoot,
                                      BigDecimal sumatoriaTributo, String codigoTipoTributo, String codigoMoneda) {

        if (sumatoriaTributo != null) {

            Element taxTotalElement = appendChild(document, elementRoot, "cac:TaxTotal");
            appendChild(document, taxTotalElement, "cbc:TaxAmount", UtilFormat.format(sumatoriaTributo))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxSubTotalElement = appendChild(document, taxTotalElement, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(sumatoriaTributo))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            Element taxSchemeElement = appendChild(document, taxCategoryElement, "cac:TaxScheme");
            Tipo tipoTributo = null;

            switch (codigoTipoTributo) {

                case ConstantesSunat.CODIGO_TRIBUTO_IGV:
                    tipoTributo = ConstantesSunat.TRIBUTO_IGV;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_ISC:
                    tipoTributo = ConstantesSunat.TRIBUTO_ISC;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_OTROS:
                    tipoTributo = ConstantesSunat.TRIBUTO_OTROS;
                    break;

            }
            appendChild(document, taxSchemeElement, "cbc:ID", tipoTributo.getId());
            appendChild(document, taxSchemeElement, "cbc:Name", tipoTributo.getName());
            appendChild(document, taxSchemeElement, "cbc:TaxTypeCode", tipoTributo.getTypeCode());
        }
    }

    private void appendChildDocumentReference(Document document, Element despatchDocumentReferenceElement,
                                              String numeroDocumentoReferencia, String tipoDocumentoReferencia) {

        appendChild(document, despatchDocumentReferenceElement, "cbc:ID", numeroDocumentoReferencia);
        appendChild(document, despatchDocumentReferenceElement, "cbc:DocumentTypeCode", tipoDocumentoReferencia);
    }

    private void appendChildItem(Document document, Element invoiceLineElement, String descripcion,
                                 String codigoProducto, String codigoProductoSunat) {

        if (descripcion != null || codigoProducto != null || codigoProductoSunat != null) {
            Element itemElement = appendChild(document, invoiceLineElement, "cac:Item");

            if (StringUtils.isNotBlank(descripcion)) {
                appendChild(document, itemElement, "cbc:Description", descripcion);
            }
            if (StringUtils.isNotBlank(codigoProducto)) {
                Element sellersItemIdentificationElement = appendChild(document, itemElement,
                        "cac:SellersItemIdentification");
                appendChild(document, sellersItemIdentificationElement, "cbc:ID", codigoProducto);
            }
            if (StringUtils.isNotBlank(codigoProductoSunat)) {
                Element standarItemIdentificationElement = appendChild(document, itemElement,
                        "cac:StandardItemIdentification");
                appendChild(document, standarItemIdentificationElement, "cbc:ID", codigoProductoSunat);
            }
        }
    }

    private void appendChildPricingReference(Document document, Element pricingReferenceElement,
                                             BigDecimal monto, String tipoPrecio, String codigoMoneda) {

        if (monto != null) {
            Element alternativeConditionElement = appendChild(document, pricingReferenceElement,
                    "cac:AlternativeConditionPrice");
            Element priceAmountElement = appendChild(document, alternativeConditionElement,
                    "cbc:PriceAmount", UtilFormat.format(monto));
            priceAmountElement.setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);
            appendChild(document, alternativeConditionElement, "cbc:PriceTypeCode", tipoPrecio);
        }
    }

    private void addElementsChildTaxTotal(Document document, Element invoiceLineElement,
                                          BigDecimal monto, String codigoTributo, String codigoTipoIgvIsc, String codigoMoneda) {

        if (monto != null) {
            Element taxTotalElement = appendChild(document, invoiceLineElement, "cac:TaxTotal");
            Element taxAmountElement = appendChild(document, taxTotalElement, "cbc:TaxAmount", UtilFormat.format(monto));
            taxAmountElement.setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxSubTotalElement = appendChild(document, taxTotalElement, "cac:TaxSubtotal");
            appendChild(document, taxSubTotalElement, "cbc:TaxAmount", UtilFormat.format(monto))
                    .setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, codigoMoneda);

            Element taxCategoryElement = appendChild(document, taxSubTotalElement, "cac:TaxCategory");
            Tipo tipoTributo = null;

            switch (codigoTributo) {

                case ConstantesSunat.CODIGO_TRIBUTO_IGV:
                    appendChild(document, taxCategoryElement, "cbc:TaxExemptionReasonCode", codigoTipoIgvIsc);
                    tipoTributo = ConstantesSunat.TRIBUTO_IGV;
                    break;
                case ConstantesSunat.CODIGO_TRIBUTO_ISC:
                    appendChild(document, taxCategoryElement, "cbc:TierRange", codigoTipoIgvIsc);
                    tipoTributo = ConstantesSunat.TRIBUTO_ISC;
                    break;

            }

            Element taxSchemeElement = appendChild(document, taxCategoryElement, "cac:TaxScheme");
            appendChild(document, taxSchemeElement, "cbc:ID", tipoTributo.getId());
            appendChild(document, taxSchemeElement, "cbc:Name", tipoTributo.getName());
            appendChild(document, taxSchemeElement, "cbc:TaxTypeCode", tipoTributo.getTypeCode());
        }
    }

    private Map<String, String> getAttributesElementRoot() {

        Map<String, String> atributos = new HashMap<String, String>();

        atributos.put("xmlns", "urn:oasis:names:specification:ubl:schema:xsd:DebitNote-2");
        atributos.put("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        atributos.put("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
        atributos.put("xmlns:ccts", "urn:un:unece:uncefact:documentation:2");
        atributos.put("xmlns:ds", "http://www.w3.org/2000/09/xmldsig#");
        atributos.put("xmlns:ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
        atributos.put("xmlns:qdt", "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2");
        atributos.put("xmlns:sac", "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1");
        atributos.put("xmlns:udt", "urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2");
        atributos.put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        return atributos;
    }
}
