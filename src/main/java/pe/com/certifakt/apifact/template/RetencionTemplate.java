package pe.com.certifakt.apifact.template;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pe.com.certifakt.apifact.bean.DocumentCpe;
import pe.com.certifakt.apifact.bean.OtherDocumentCpe;
import pe.com.certifakt.apifact.bean.Signature;
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
public class RetencionTemplate extends TemplateSunat {

    public String buildRetention(OtherDocumentCpe retencion) throws TemplateException {

        String stringXMLGenerate = null;
        Document document;
        Element elementRoot;

        try {
            Signature signature = buildSignature(
                    retencion.getDenominacionEmisor(),
                    retencion.getNumeroDocumentoIdentidadEmisor()
            );
            document = createDocument();
            elementRoot = addElementRoot(document, ROOT_RETENTION, getAttributesElementRoot());

            appendChild(document, elementRoot, "ext:UBLExtensions");
            appendChild(document, elementRoot, "cbc:UBLVersionID", ConstantesSunat.UBL_VERSION_ID_RETENTION);
            appendChild(document, elementRoot, "cbc:CustomizationID", ConstantesSunat.CUSTOMIZATION_ID_RETENTION);
            appendChildSignature(document, elementRoot, signature);
            appendChild(document, elementRoot, "cbc:ID", retencion.getSerie() + "-" + retencion.getNumero());
            appendChild(document, elementRoot, "cbc:IssueDate", retencion.getFechaEmision());
            if (StringUtils.isNotBlank(retencion.getHoraEmision())) {
                appendChild(document, elementRoot, "cbc:IssueTime", retencion.getHoraEmision());
            }

            Element agentPartyElement = appendChild(document, elementRoot, "cac:AgentParty");
            appendChildParty(document, agentPartyElement,
                    retencion.getTipoDocumentoIdentidadEmisor(),
                    retencion.getNumeroDocumentoIdentidadEmisor(),
                    retencion.getNombreComercialEmisor(),
                    retencion.getDenominacionEmisor(),
                    retencion.getUbigeoDomicilioFiscalEmisor(),
                    retencion.getUrbanizacionDomicilioFiscalEmisor(),
                    retencion.getDireccionCompletaDomicilioFiscalEmisor(),
                    retencion.getCodigoPaisDomicilioFiscalEmisor(),
                    retencion.getDepartamentoDomicilioFiscalEmisor(),
                    retencion.getProvinciaDomicilioFiscalEmisor(),
                    retencion.getDistritoDomicilioFiscalEmisor());

            Element receiverPartyElement = appendChild(document, elementRoot, "cac:ReceiverParty");
            appendChildParty(document, receiverPartyElement,
                    retencion.getTipoDocumentoIdentidadReceptor(),
                    retencion.getNumeroDocumentoIdentidadReceptor(),
                    retencion.getNombreComercialReceptor(),
                    retencion.getDenominacionReceptor(),
                    retencion.getUbigeoDomicilioFiscalReceptor(),
                    retencion.getUrbanizacionDomicilioFiscalReceptor(),
                    retencion.getDireccionCompletaDomicilioFiscalReceptor(),
                    retencion.getCodigoPaisDomicilioFiscalReceptor(),
                    retencion.getDepartamentoDomicilioFiscalReceptor(),
                    retencion.getProvinciaDomicilioFiscalReceptor(),
                    retencion.getDistritoDomicilioFiscalReceptor());

            appendChild(document, elementRoot, "sac:SUNATRetentionSystemCode", retencion.getRegimen());
            appendChild(document, elementRoot, "sac:SUNATRetentionPercent", retencion.getTasa());
            if (StringUtils.isNotBlank(retencion.getObservaciones())) {
                appendChild(document, elementRoot, "cbc:Note", retencion.getObservaciones());
            }
            appendChild(document, elementRoot, "cbc:TotalInvoiceAmount", UtilFormat.format(retencion.getImporteTotalRetenidoPercibido())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, retencion.getCodigoMoneda());
            appendChild(document, elementRoot, "sac:SUNATTotalPaid", UtilFormat.format(retencion.getImporteTotalPagadoCobrado())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, retencion.getCodigoMoneda());
            if (retencion.getMontoRedondeoImporteTotal() != null) {
                appendChild(document, elementRoot, "cbc:PayableRoundingAmount", UtilFormat.format(retencion.getMontoRedondeoImporteTotal())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, retencion.getCodigoMoneda());
            }

            appendChildDocumetsRetention(document, elementRoot, retencion.getDocumentosRelacionados());

            stringXMLGenerate = formatXML(buildStringFromDOM(document));

        } catch (Exception e) {
            throw new TemplateException("Error generando plantilla Retencion:" + e.getMessage());
        }

        return stringXMLGenerate;
    }

    private void appendChildDocumetsRetention(
            Document document, Element elementRoot,
            List<DocumentCpe> documentosRelacionados) {

        for (DocumentCpe comprobante : documentosRelacionados) {

            Element retentionDocumentReferenceElement = appendChild(document, elementRoot,
                    "sac:SUNATRetentionDocumentReference");
            appendChildDatosDocumentoRelacionado(
                    document,
                    retentionDocumentReferenceElement,
                    comprobante.getSerieDocumentoRelacionado(),
                    comprobante.getNumeroDocumentoRelacionado(),
                    comprobante.getTipoDocumentoRelacionado(),
                    comprobante.getFechaEmisionDocumentoRelacionado(),
                    comprobante.getImporteTotalDocumentoRelacionado(),
                    comprobante.getMonedaDocumentoRelacionado()
            );
            appendChildDatosPago(
                    document,
                    retentionDocumentReferenceElement,
                    comprobante.getNumeroPagoCobro(),
                    comprobante.getImportePagoSinRetencionCobro(),
                    comprobante.getMonedaPagoCobro(),
                    comprobante.getFechaPagoCobro()
            );
            Element retentionInformationElement = appendChild(document, retentionDocumentReferenceElement,
                    "sac:SUNATRetentionInformation");
            appendChildDatosRetencion(
                    document,
                    retentionInformationElement,
                    comprobante.getImporteRetenidoPercibido(),
                    comprobante.getMonedaImporteRetenidoPercibido(),
                    comprobante.getFechaRetencionPercepcion(),
                    comprobante.getImporteTotalToPagarCobrar(),
                    comprobante.getMonedaImporteTotalToPagarCobrar()
            );
            appendChildDatosTipoCambio(
                    document,
                    retentionInformationElement,
                    comprobante.getMonedaReferenciaTipoCambio(),
                    comprobante.getMonedaObjetivoTasaCambio(),
                    comprobante.getTipoCambio(),
                    comprobante.getFechaCambio()
            );
        }
    }

    private void appendChildDatosDocumentoRelacionado(Document document, Element retentionDocumentReferenceElement,
                                                      String serieDocumentoRelacionado, Integer numeroDocumentoRelacionado, String tipoDocumentoRelacionado,
                                                      String fechaEmisionDocumentoRelacionado, BigDecimal importeTotalDocumentoRelacionado,
                                                      String monedaDocumentoRelacionado) {

        appendChild(document, retentionDocumentReferenceElement, "cbc:ID", serieDocumentoRelacionado + "-" +
                numeroDocumentoRelacionado).setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID,
                tipoDocumentoRelacionado);
        appendChild(document, retentionDocumentReferenceElement, "cbc:IssueDate",
                fechaEmisionDocumentoRelacionado);
        appendChild(document, retentionDocumentReferenceElement, "cbc:TotalInvoiceAmount",
                UtilFormat.format(importeTotalDocumentoRelacionado)).setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID,
                monedaDocumentoRelacionado);

    }

    private void appendChildDatosPago(Document document, Element retentionDocumentReferenceElement,
                                      String numeroPago, BigDecimal importePagoSinRetencion, String monedaPago, String fechaPago) {

        Element paymentElement = appendChild(document, retentionDocumentReferenceElement, "cac:Payment");
        appendChild(document, paymentElement, "cbc:ID", numeroPago);
        appendChild(document, paymentElement, "cbc:PaidAmount", UtilFormat.format(importePagoSinRetencion)).
                setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, monedaPago);
        appendChild(document, paymentElement, "cbc:PaidDate", fechaPago);

    }

    private void appendChildDatosRetencion(Document document, Element retentionInformationElement,
                                           BigDecimal importeRetenido, String monedaImporteRetenido, String fechaRetencion,
                                           BigDecimal importeTotalToPagar, String monedaImporteTotalToPagar) {

        appendChild(document, retentionInformationElement, "sac:SUNATRetentionAmount", UtilFormat.format(importeRetenido)).
                setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, monedaImporteRetenido);
        appendChild(document, retentionInformationElement, "sac:SUNATRetentionDate", fechaRetencion);
        appendChild(document, retentionInformationElement, "sac:SUNATNetTotalPaid", UtilFormat.format(importeTotalToPagar)).
                setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, monedaImporteTotalToPagar);

    }

    private void appendChildDatosTipoCambio(Document document, Element retentionInformationElement,
                                            String monedaReferenciaTipoCambio, String monedaObjetivoTasaCambio, BigDecimal tipoCambio,
                                            String fechaCambio) {

        boolean existeDatosTipoCambio = validacionExistenciaDatosTipoCambio(monedaReferenciaTipoCambio,
                monedaObjetivoTasaCambio, tipoCambio, fechaCambio);
        if (existeDatosTipoCambio) {

            Element exchangeRateElement = appendChild(document, retentionInformationElement,
                    "cac:ExchangeRate");
            if (StringUtils.isNotBlank(monedaReferenciaTipoCambio)) {
                appendChild(document, exchangeRateElement, "cbc:SourceCurrencyCode",
                        monedaReferenciaTipoCambio);
            }
            if (StringUtils.isNotBlank(monedaObjetivoTasaCambio)) {
                appendChild(document, exchangeRateElement, "cbc:TargetCurrencyCode",
                        monedaObjetivoTasaCambio);
            }
            if (tipoCambio != null && tipoCambio.compareTo(BigDecimal.ZERO) > 0) {
                appendChild(document, exchangeRateElement, "cbc:CalculationRate", UtilFormat.format(tipoCambio));
            }
            if (StringUtils.isNotBlank(fechaCambio)) {
                appendChild(document, exchangeRateElement, "cbc:Date", fechaCambio);
            }
        }
    }

    private boolean validacionExistenciaDatosTipoCambio(String monedaReferenciaTipoCambio,
                                                        String monedaObjetivoTasaCambio, BigDecimal tipoCambio, String fechaCambio) {

        boolean existeDatos = false;

        if (StringUtils.isNotBlank(monedaReferenciaTipoCambio)) {
            return true;
        }
        if (StringUtils.isNotBlank(monedaObjetivoTasaCambio)) {
            return true;
        }
        if (StringUtils.isNotBlank(fechaCambio)) {
            return true;
        }
        if (tipoCambio != null && tipoCambio.compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }

        return existeDatos;
    }

    private void appendChildParty(
            Document document, Element partyElement,
            String tipoDocumentoIdentidad, String numeroDocumentoIdentidad,
            String nombreComercial, String denominacion,
            String ubigeo, String urbanizacion, String direccion, String codigoPais,
            String departamento, String provincia, String distrito) {

        boolean existeDireccion = existeDatosDireccion(
                ubigeo, urbanizacion, direccion, codigoPais,
                departamento, provincia, distrito);

        Element partyIdentificationElement = appendChild(document, partyElement, "cac:PartyIdentification");
        appendChild(document, partyIdentificationElement, "cbc:ID", numeroDocumentoIdentidad).
                setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID, tipoDocumentoIdentidad);

        if (StringUtils.isNotBlank(nombreComercial)) {

            Element partyNameElement = appendChild(document, partyElement, "cac:PartyName");
            appendChild(document, partyNameElement, "cbc:Name", nombreComercial);
        }

        if (existeDireccion) {

            Element postalAddressElement = appendChild(document, partyElement,
                    "cac:PostalAddress");
            if (StringUtils.isNotBlank(ubigeo)) {
                appendChild(document, postalAddressElement, "cbc:ID", ubigeo);
            }
            if (StringUtils.isNotBlank(direccion)) {
                appendChild(document, postalAddressElement, "cbc:StreetName", direccion);
            }
            if (StringUtils.isNotBlank(urbanizacion)) {
                appendChild(document, postalAddressElement, "cbc:CitySubdivisionName", urbanizacion);
            }
            if (StringUtils.isNotBlank(provincia)) {
                appendChild(document, postalAddressElement, "cbc:CityName", provincia);
            }
            if (StringUtils.isNotBlank(departamento)) {
                appendChild(document, postalAddressElement, "cbc:CountrySubentity", departamento);
            }
            if (StringUtils.isNotBlank(distrito)) {
                appendChild(document, postalAddressElement, "cbc:District", distrito);
            }
            if (StringUtils.isNotBlank(codigoPais)) {
                Element countryElement = appendChild(document, postalAddressElement,
                        "cac:Country");
                appendChild(document, countryElement, "cbc:IdentificationCode", codigoPais);
            }
        }
        Element partyLegalEntityElement = appendChild(document, partyElement, "cac:PartyLegalEntity");
        appendChild(document, partyLegalEntityElement, "cbc:RegistrationName", denominacion);
    }

    private boolean existeDatosDireccion(String ubigeo, String urbanizacion, String direccion,
                                         String codigoPais, String departamento, String provincia, String distrito) {

        boolean existeDatos = false;

        if (StringUtils.isNotBlank(ubigeo)) {
            return true;
        }
        if (StringUtils.isNotBlank(direccion)) {
            return true;
        }
        if (StringUtils.isNotBlank(departamento)) {
            return true;
        }
        if (StringUtils.isNotBlank(provincia)) {
            return true;
        }
        if (StringUtils.isNotBlank(distrito)) {
            return true;
        }
        if (StringUtils.isNotBlank(urbanizacion)) {
            return true;
        }
        if (StringUtils.isNotBlank(codigoPais)) {
            return true;
        }

        return existeDatos;
    }

    private Signature buildSignature(String denominacionEmisor, String rucEmisor) {

        Signature signature = new Signature();

        signature.setId("IDSignKG");
        signature.setUri("#signatureKG");
        signature.setDenominacionEmisor(denominacionEmisor);
        signature.setRucEmisor(rucEmisor);

        return signature;
    }

    private Map<String, String> getAttributesElementRoot() {

        Map<String, String> atributos = new HashMap<String, String>();

        atributos.put("xmlns", "urn:sunat:names:specification:ubl:peru:schema:xsd:Retention-1");
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