package pe.com.certifakt.apifact.template;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.exception.TemplateException;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.UtilFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;

import static pe.com.certifakt.apifact.util.UtilXML.appendChild;
import static pe.com.certifakt.apifact.util.UtilXML.formatXML;

@Component
public class BoletaTemplate {


    public String buildBoleta(PaymentVoucher invoice) throws TemplateException {
        String xml = "";
        DOMSource source;
        StringWriter writer;
        int correlativoItem = 1;
        BigDecimal montoAnticiposTotalValorVenta = BigDecimal.ZERO;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();

            Element invoiceRootElement = doc.createElement("Invoice");
            doc.appendChild(invoiceRootElement);

            Attr xmlns = doc.createAttribute("xmlns");
            xmlns.setValue("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
            invoiceRootElement.setAttributeNode(xmlns);

            Attr xmlnscac = doc.createAttribute("xmlns:cac");
            xmlnscac.setValue("urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
            invoiceRootElement.setAttributeNode(xmlnscac);

            Attr xmlnscbc = doc.createAttribute("xmlns:cbc");
            xmlnscbc.setValue("urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");
            invoiceRootElement.setAttributeNode(xmlnscbc);

            Attr xmlnsccts = doc.createAttribute("xmlns:ccts");
            xmlnsccts.setValue("urn:un:unece:uncefact:documentation:2");
            invoiceRootElement.setAttributeNode(xmlnsccts);

            Attr xmlnsds = doc.createAttribute("xmlns:ds");
            xmlnsds.setValue("http://www.w3.org/2000/09/xmldsig#");
            invoiceRootElement.setAttributeNode(xmlnsds);

            Attr xmlnsext = doc.createAttribute("xmlns:ext");
            xmlnsext.setValue("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
            invoiceRootElement.setAttributeNode(xmlnsext);

            Attr xmlnsqdt = doc.createAttribute("xmlns:qdt");
            xmlnsqdt.setValue("urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2");
            invoiceRootElement.setAttributeNode(xmlnsqdt);

            Attr xmlnssac = doc.createAttribute("xmlns:sac");
            xmlnssac.setValue("urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1");
            invoiceRootElement.setAttributeNode(xmlnssac);

            Attr xmlnsudt = doc.createAttribute("xmlns:udt");
            xmlnsudt.setValue("urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2");
            invoiceRootElement.setAttributeNode(xmlnsudt);

            Attr xmlnsxsi = doc.createAttribute("xmlns:xsi");
            xmlnsxsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
            invoiceRootElement.setAttributeNode(xmlnsxsi);

            Element extUBLExtensions = appendChild(doc, invoiceRootElement, "ext:UBLExtensions");
            Element extUBLExtension = appendChild(doc, extUBLExtensions, "ext:UBLExtension");
            Element extExtensionContent = appendChild(doc, extUBLExtension, "ext:ExtensionContent");
            Element sacAdditionalInformation = appendChild(doc, extExtensionContent,
                    "sac:AdditionalInformation");

            if (invoice.getTotalValorVentaGravada() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_GRAVADA);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(invoice.getTotalValorVentaGravada())).
                        setAttribute("currencyID", invoice.getCodigoMoneda());
            }
            if (invoice.getTotalValorVentaInafecta() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_INAFECTA);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(invoice.getTotalValorVentaInafecta())).
                        setAttribute("currencyID", invoice.getCodigoMoneda());
            }
            if (invoice.getTotalValorVentaExonerada() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_EXONERADA);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(invoice.getTotalValorVentaExonerada())).
                        setAttribute("currencyID", invoice.getCodigoMoneda());
            }
            if (invoice.getTotalValorVentaGratuita() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID",
                        ConstantesSunat.TOTAL_VALOR_VENTA_OPE_GRATUITA);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(invoice.getTotalValorVentaGratuita())).
                        setAttribute("currencyID", invoice.getCodigoMoneda());
            }
            if (invoice.getTotalDescuento() != null) {
                Element sacAdditionalMonetaryTotal = appendChild(doc, sacAdditionalInformation,
                        "sac:AdditionalMonetaryTotal");
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:ID", ConstantesSunat.TOTAL_DESCUENTO);
                appendChild(doc, sacAdditionalMonetaryTotal, "cbc:PayableAmount",
                        UtilFormat.format(invoice.getTotalDescuento())).
                        setAttribute("currencyID", invoice.getCodigoMoneda());
            }

            if (StringUtils.isNotBlank(invoice.getCodigoTipoOperacion())) {
                Element sunatTransactionElement = appendChild(doc, sacAdditionalInformation,
                        "sac:SUNATTransaction");
                appendChild(doc, sunatTransactionElement, "cbc:ID", invoice.getCodigoTipoOperacion());
            }

            appendChild(doc, invoiceRootElement, "cbc:UBLVersionID", "2.0");
            appendChild(doc, invoiceRootElement, "cbc:CustomizationID", "1.0");
            appendChild(doc, invoiceRootElement, "cbc:ID", invoice.getSerie() + "-" + invoice.getNumero());
            appendChild(doc, invoiceRootElement, "cbc:IssueDate", invoice.getFechaEmision());
            if (StringUtils.isNotBlank(invoice.getHoraEmision())) {
                appendChild(doc, invoiceRootElement, "cbc:IssueTime", invoice.getHoraEmision());
            }
            appendChild(doc, invoiceRootElement, "cbc:InvoiceTypeCode", invoice.getTipoComprobante());
            appendChild(doc, invoiceRootElement, "cbc:DocumentCurrencyCode", invoice.getCodigoMoneda());

            if (invoice.getGuiasRelacionadas() != null && !invoice.getGuiasRelacionadas().isEmpty()) {
                for (GuiaRelacionada guiaRelacionada : invoice.getGuiasRelacionadas()) {
                    appendChilDespatchDocumentReference(doc, invoiceRootElement,
                            guiaRelacionada.getSerieNumeroGuia(),
                            guiaRelacionada.getCodigoTipoGuia());
                }
            }

            appendChilAdditionalDocumentReference(doc, invoiceRootElement,
                    invoice.getSerieNumeroOtroDocumentoRelacionado(),
                    invoice.getCodigoTipoOtroDocumentoRelacionado());

            Element cacSignature = appendChild(doc, invoiceRootElement, "cac:Signature");
            appendChild(doc, cacSignature, "cbc:ID", "IDSignKG");

            Element cacSignatoryParty = appendChild(doc, cacSignature, "cac:SignatoryParty");

            Element cacPartyIdentification = appendChild(doc, cacSignatoryParty, "cac:PartyIdentification");
            appendChild(doc, cacPartyIdentification, "cbc:ID", invoice.getRucEmisor());

            Element cacPartyName = appendChild(doc, cacSignatoryParty, "cac:PartyName");
            appendChild(doc, cacPartyName, "cbc:Name", invoice.getDenominacionEmisor());

            Element cacDigitalSignatureAttachment = appendChild(doc, cacSignature, "cac:DigitalSignatureAttachment");
            Element cacExternalReference = appendChild(doc, cacDigitalSignatureAttachment, "cac:ExternalReference");
            appendChild(doc, cacExternalReference, "cbc:URI", "#SignatureKG");

            Element cacAccountingSupplierParty = appendChild(doc, invoiceRootElement, "cac:AccountingSupplierParty");
            appendChild(doc, cacAccountingSupplierParty, "cbc:CustomerAssignedAccountID", invoice.getRucEmisor());
            appendChild(doc, cacAccountingSupplierParty, "cbc:AdditionalAccountID", invoice.getTipoDocumentoEmisor());


            Element cacPartySupplier = appendChild(doc, cacAccountingSupplierParty, "cac:Party");
            if (StringUtils.isNotBlank(invoice.getNombreComercialEmisor())) {
                Element cacPartyNameElement = appendChild(doc, cacPartySupplier, "cac:PartyName");
                appendChild(doc, cacPartyNameElement, "cbc:Name", invoice.getNombreComercialEmisor());
            }

            Element cacPartyLegalEntity = appendChild(doc, cacPartySupplier, "cac:PartyLegalEntity");

            if (StringUtils.isNotBlank(invoice.getCodigoLocalAnexoEmisor())) {
                Element cacRegistrationAddress = appendChild(doc, cacPartyLegalEntity, "cac:RegistrationAddress");
                appendChild(doc, cacRegistrationAddress, "cbc:AddressTypeCode", invoice.getCodigoLocalAnexoEmisor());
            }
            appendChild(doc, cacPartyLegalEntity, "cbc:RegistrationName", invoice.getDenominacionEmisor());

            if (StringUtils.isNotBlank(invoice.getNumeroDocumentoReceptor()) || StringUtils.isNotBlank(invoice.getTipoDocumentoReceptor()) ||
                    StringUtils.isNotBlank(invoice.getDenominacionReceptor())) {

                Element cacAccountingCustomerParty = appendChild(doc, invoiceRootElement, "cac:AccountingCustomerParty");
                if (StringUtils.isNotBlank(invoice.getNumeroDocumentoReceptor())) {
                    appendChild(doc, cacAccountingCustomerParty, "cbc:CustomerAssignedAccountID", invoice.getNumeroDocumentoReceptor());
                }
                if (StringUtils.isNotBlank(invoice.getTipoDocumentoReceptor())) {
                    appendChild(doc, cacAccountingCustomerParty, "cbc:AdditionalAccountID", invoice.getTipoDocumentoReceptor());
                }
                if (StringUtils.isNotBlank(invoice.getDenominacionReceptor())) {
                    Element cacPartyCustomer = appendChild(doc, cacAccountingCustomerParty, "cac:Party");
                    cacPartyLegalEntity = appendChild(doc, cacPartyCustomer, "cac:PartyLegalEntity");
                    appendChild(doc, cacPartyLegalEntity, "cbc:RegistrationName", invoice.getDenominacionReceptor());
                }

                //[Anticipos con deduccion]
                if (StringUtils.isNotBlank(invoice.getCodigoTipoOperacion()) &&
                        invoice.getCodigoTipoOperacion().equals(ConstantesSunat.CODIGO_TIPO_OPERACION_VENTA_INTERNA_ANTICIPOS) &&
                        invoice.getAnticipos() != null && !invoice.getAnticipos().isEmpty()) {

                    for (Anticipo anticipo : invoice.getAnticipos()) {

                        Element prepaidPayment = appendChild(doc, invoiceRootElement, "cac:PrepaidPayment");
                        appendChild(doc, prepaidPayment, "cbc:ID",
                                String.format("%s-%s", anticipo.getSerieAnticipo(), anticipo.getNumeroAnticipo())).
                                setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID, anticipo.getTipoDocumentoAnticipo());
                        appendChild(doc, prepaidPayment, "cbc:PaidAmount", UtilFormat.format(anticipo.getMontoAnticipado())).
                                setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, invoice.getCodigoMoneda());
                        appendChild(doc, prepaidPayment, "cbc:InstructionID", invoice.getRucEmisor()).
                                setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID, invoice.getTipoDocumentoEmisor());
                        ;

                        montoAnticiposTotalValorVenta = montoAnticiposTotalValorVenta.add(anticipo.getMontoAnticipado());
                        invoice.setTotalAnticipos(montoAnticiposTotalValorVenta);
                    }
                }
            }

            //[Anticipos]
            appendChildSumatoria(doc, invoiceRootElement, invoice.getTotalIgv(),
                    ConstantesSunat.CODIGO_TRIBUTO_IGV, invoice.getCodigoMoneda());
            appendChildSumatoria(doc, invoiceRootElement, invoice.getTotalIsc(),
                    ConstantesSunat.CODIGO_TRIBUTO_ISC, invoice.getCodigoMoneda());
            appendChildSumatoria(doc, invoiceRootElement, invoice.getTotalOtrostributos(),
                    ConstantesSunat.CODIGO_TRIBUTO_OTROS, invoice.getCodigoMoneda());

            Element legalMonetaryTotalElement = appendChild(doc, invoiceRootElement, "cac:LegalMonetaryTotal");
            if (invoice.getDescuentoGlobales() != null) {
                appendChild(doc, legalMonetaryTotalElement, "cbc:AllowanceTotalAmount",
                        UtilFormat.format(invoice.getDescuentoGlobales())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, invoice.getCodigoMoneda());
            }
            if (invoice.getSumatoriaOtrosCargos() != null) {
                appendChild(doc, legalMonetaryTotalElement, "cbc:ChargeTotalAmount",
                        UtilFormat.format(invoice.getSumatoriaOtrosCargos())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, invoice.getCodigoMoneda());
            }
            if (invoice.getTotalAnticipos() != null) {
                appendChild(doc, legalMonetaryTotalElement, "cbc:PrepaidAmount",
                        UtilFormat.format(invoice.getTotalAnticipos())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, invoice.getCodigoMoneda());
            }
            appendChild(doc, legalMonetaryTotalElement, "cbc:PayableAmount",
                    UtilFormat.format(invoice.getImporteTotalVenta())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, invoice.getCodigoMoneda());

            for (PaymentVoucherLine item : invoice.getItems()) {

                Element cacInvoiceLine = appendChild(doc, invoiceRootElement, "cac:InvoiceLine");
                appendChild(doc, cacInvoiceLine, "cbc:ID", correlativoItem);
                appendChild(doc, cacInvoiceLine, "cbc:InvoicedQuantity", UtilFormat.format(item.getCantidad())).
                        setAttribute("unitCode", item.getCodigoUnidadMedida());
                appendChild(doc, cacInvoiceLine, "cbc:LineExtensionAmount", UtilFormat.format(item.getValorVenta())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, invoice.getCodigoMoneda());

                Element pricingReferenceElement = appendChild(doc, cacInvoiceLine, "cac:PricingReference");
                appendChildPricingReference(doc, pricingReferenceElement, item.getPrecioVentaUnitario(),
                        ConstantesSunat.CODIGO_TIPO_PRECIO_PRECIO_UNITARIO, invoice.getCodigoMoneda());
                appendChildPricingReference(doc, pricingReferenceElement, item.getValorReferencialUnitario(),
                        ConstantesSunat.CODIGO_TIPO_PRECIO_VALOR_REFERENCIAL, invoice.getCodigoMoneda());

                appendChildAllowance(doc, cacInvoiceLine, item.getDescuento(), invoice.getCodigoMoneda());

                addElementsChildTaxTotal(doc, cacInvoiceLine, item.getIgv(),
                        ConstantesSunat.CODIGO_TRIBUTO_IGV, item.getCodigoTipoAfectacionIGV(),
                        invoice.getCodigoMoneda());
                addElementsChildTaxTotal(doc, cacInvoiceLine, item.getIsc(),
                        ConstantesSunat.CODIGO_TRIBUTO_ISC, item.getCodigoTipoCalculoISC(),
                        invoice.getCodigoMoneda());

                appendChildItem(doc, cacInvoiceLine, item.getDescripcion(), item.getCodigoProducto(),
                        item.getCodigoProductoSunat());
                Element cbcPrice = appendChild(doc, cacInvoiceLine, "cac:Price");
                appendChild(doc, cbcPrice, "cbc:PriceAmount", UtilFormat.format(item.getValorUnitario())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, invoice.getCodigoMoneda());

                item.setNumeroItem(correlativoItem);
                correlativoItem++;
            }


            source = new DOMSource(doc);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            transformer.transform(source, result);

            xml = formatXML(writer.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TemplateException(ex.getMessage());
        }
        return xml;
    }

    private void appendChildAllowance(Document doc, Element cacInvoiceLine, BigDecimal descuento, String moneda) {

        if (descuento != null) {
            Element allowanceElement = appendChild(doc, cacInvoiceLine, "cac:AllowanceCharge");
            appendChild(doc, allowanceElement, "cbc:ChargeIndicator", "false");
            appendChild(doc, allowanceElement, "cbc:Amount", UtilFormat.format(descuento)).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, moneda);
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

    private void appendChildDocumentReference(Document document, Element despatchDocumentReferenceElement,
                                              String numeroDocumentoReferencia, String tipoDocumentoReferencia) {

        appendChild(document, despatchDocumentReferenceElement, "cbc:ID", numeroDocumentoReferencia);
        appendChild(document, despatchDocumentReferenceElement, "cbc:DocumentTypeCode", tipoDocumentoReferencia);
    }

    private void appendChildItem(Document document, Element invoiceLineElement, String descripcion,
                                 String codigoProducto, String codigoProductoSunat) {

        Element itemElement = appendChild(document, invoiceLineElement, "cac:Item");

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
        appendChild(document, itemElement, "cbc:Description", descripcion);
    }

    private void appendChildPricingReference(Document document, Element pricingReferenceElement,
                                             BigDecimal monto, String tipoPrecio, String codigoMoneda) {

        if (monto != null) {
            Element alternativeConditionElement = appendChild(document, pricingReferenceElement, "cac:AlternativeConditionPrice");
            Element priceAmountElement = appendChild(document, alternativeConditionElement, "cbc:PriceAmount", UtilFormat.format(monto));
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
}
