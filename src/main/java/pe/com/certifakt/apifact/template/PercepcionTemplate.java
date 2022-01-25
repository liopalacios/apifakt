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
public class PercepcionTemplate extends TemplateSunat {

    public String buildPerception(OtherDocumentCpe perception) throws TemplateException {

        String stringXMLGenerate = null;
        Document document;
        Element elementRoot;

        try {
            Signature signature = buildSignature(
                    perception.getDenominacionEmisor(),
                    perception.getNumeroDocumentoIdentidadEmisor()
            );
            document = createDocument();
            elementRoot = addElementRoot(document, ROOT_PERCEPTION, getAttributesElementRoot());

            appendChild(document, elementRoot, "ext:UBLExtensions");
            appendChild(document, elementRoot, "cbc:UBLVersionID", ConstantesSunat.UBL_VERSION_ID_PERCEPTION);
            appendChild(document, elementRoot, "cbc:CustomizationID", ConstantesSunat.CUSTOMIZATION_ID_PERCEPTION);
            appendChildSignature(document, elementRoot, signature);
            appendChild(document, elementRoot, "cbc:ID", perception.getSerie() + "-" + perception.getNumero());
            appendChild(document, elementRoot, "cbc:IssueDate", perception.getFechaEmision());
            if (StringUtils.isNotBlank(perception.getHoraEmision())) {
                appendChild(document, elementRoot, "cbc:IssueTime", perception.getHoraEmision());
            }

            Element agentPartyElement = appendChild(document, elementRoot, "cac:AgentParty");
            appendChildParty(document, agentPartyElement,
                    perception.getTipoDocumentoIdentidadEmisor(),
                    perception.getNumeroDocumentoIdentidadEmisor(),
                    perception.getNombreComercialEmisor(),
                    perception.getDenominacionEmisor(),
                    perception.getUbigeoDomicilioFiscalEmisor(),
                    perception.getUrbanizacionDomicilioFiscalEmisor(),
                    perception.getDireccionCompletaDomicilioFiscalEmisor(),
                    perception.getCodigoPaisDomicilioFiscalEmisor(),
                    perception.getDepartamentoDomicilioFiscalEmisor(),
                    perception.getProvinciaDomicilioFiscalEmisor(),
                    perception.getDistritoDomicilioFiscalEmisor());

            Element receiverPartyElement = appendChild(document, elementRoot, "cac:ReceiverParty");
            appendChildParty(document, receiverPartyElement,
                    perception.getTipoDocumentoIdentidadReceptor(),
                    perception.getNumeroDocumentoIdentidadReceptor(),
                    perception.getNombreComercialReceptor(),
                    perception.getDenominacionReceptor(),
                    perception.getUbigeoDomicilioFiscalReceptor(),
                    perception.getUrbanizacionDomicilioFiscalReceptor(),
                    perception.getDireccionCompletaDomicilioFiscalReceptor(),
                    perception.getCodigoPaisDomicilioFiscalReceptor(),
                    perception.getDepartamentoDomicilioFiscalReceptor(),
                    perception.getProvinciaDomicilioFiscalReceptor(),
                    perception.getDistritoDomicilioFiscalReceptor());

            appendChild(document, elementRoot, "sac:SUNATPerceptionSystemCode", perception.getRegimen());
            appendChild(document, elementRoot, "sac:SUNATPerceptionPercent", perception.getTasa());
            if (StringUtils.isNotBlank(perception.getObservaciones())) {
                appendChild(document, elementRoot, "cbc:Note", perception.getObservaciones());
            }
            appendChild(document, elementRoot, "cbc:TotalInvoiceAmount", UtilFormat.format(perception.getImporteTotalRetenidoPercibido())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, perception.getCodigoMoneda());
            appendChild(document, elementRoot, "sac:SUNATTotalCashed", UtilFormat.format(perception.getImporteTotalPagadoCobrado())).
                    setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, perception.getCodigoMoneda());
            if (perception.getMontoRedondeoImporteTotal() != null) {
                appendChild(document, elementRoot, "cbc:PayableRoundingAmount", UtilFormat.format(perception.getMontoRedondeoImporteTotal())).
                        setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, perception.getCodigoMoneda());
            }

            appendChildDocumetsPerception(document, elementRoot, perception.getDocumentosRelacionados());

            stringXMLGenerate = formatXML(buildStringFromDOM(document));

        } catch (Exception e) {
            throw new TemplateException("Error generando plantilla Retencion:" + e.getMessage());
        }

        return stringXMLGenerate;
    }

    private void appendChildDocumetsPerception(
            Document document, Element elementRoot,
            List<DocumentCpe> documentosRelacionados) {

        for (DocumentCpe comprobante : documentosRelacionados) {

            Element perceptionDocumentReferenceElement = appendChild(document, elementRoot,
                    "sac:SUNATPerceptionDocumentReference");
            appendChildDatosDocumentoRelacionado(
                    document,
                    perceptionDocumentReferenceElement,
                    comprobante.getSerieDocumentoRelacionado(),
                    comprobante.getNumeroDocumentoRelacionado(),
                    comprobante.getTipoDocumentoRelacionado(),
                    comprobante.getFechaEmisionDocumentoRelacionado(),
                    comprobante.getImporteTotalDocumentoRelacionado(),
                    comprobante.getMonedaDocumentoRelacionado()
            );
            appendChildDatosCobro(
                    document,
                    perceptionDocumentReferenceElement,
                    comprobante.getNumeroPagoCobro(),
                    comprobante.getImportePagoSinRetencionCobro(),
                    comprobante.getMonedaPagoCobro(),
                    comprobante.getFechaPagoCobro()
            );
            Element perceptionInformationElement = appendChild(document, perceptionDocumentReferenceElement,
                    "sac:SUNATPerceptionInformation");
            appendChildDatosPerception(
                    document,
                    perceptionInformationElement,
                    comprobante.getImporteRetenidoPercibido(),
                    comprobante.getMonedaImporteRetenidoPercibido(),
                    comprobante.getFechaRetencionPercepcion(),
                    comprobante.getImporteTotalToPagarCobrar(),
                    comprobante.getMonedaImporteTotalToPagarCobrar()
            );
            appendChildDatosTipoCambio(
                    document,
                    perceptionInformationElement,
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

    private void appendChildDatosCobro(Document document, Element retentionDocumentReferenceElement,
                                       String numeroCobro, BigDecimal importeCobro, String monedaCobro, String fechaCobro) {

        Element paymentElement = appendChild(document, retentionDocumentReferenceElement, "cac:Payment");
        appendChild(document, paymentElement, "cbc:ID", numeroCobro);
        appendChild(document, paymentElement, "cbc:PaidAmount", UtilFormat.format(importeCobro)).
                setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, monedaCobro);
        appendChild(document, paymentElement, "cbc:PaidDate", fechaCobro);

    }

    private void appendChildDatosPerception(Document document, Element perceptionInformationElement,
                                            BigDecimal importePercibido, String monedaImportePercibido, String fechaPercepcion,
                                            BigDecimal importeTotalToCobrar, String monedaImporteTotalToCobrar) {

        appendChild(document, perceptionInformationElement, "sac:SUNATPerceptionAmount", UtilFormat.format(importePercibido)).
                setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, monedaImportePercibido);
        appendChild(document, perceptionInformationElement, "sac:SUNATPerceptionDate", fechaPercepcion);
        appendChild(document, perceptionInformationElement, "sac:SUNATNetTotalCashed", UtilFormat.format(importeTotalToCobrar)).
                setAttribute(ConstantesSunat.ATTRIBUTE_CURRENCY_ID, monedaImporteTotalToCobrar);

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

        atributos.put("xmlns", "urn:sunat:names:specification:ubl:peru:schema:xsd:Perception-1");
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