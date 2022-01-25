package pe.com.certifakt.apifact.template;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pe.com.certifakt.apifact.bean.GuiaItem;
import pe.com.certifakt.apifact.bean.GuiaRemision;
import pe.com.certifakt.apifact.bean.Signature;
import pe.com.certifakt.apifact.bean.TramoTraslado;
import pe.com.certifakt.apifact.exception.TemplateException;
import pe.com.certifakt.apifact.util.ConstantesSunat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pe.com.certifakt.apifact.util.UtilXML.appendChild;
import static pe.com.certifakt.apifact.util.UtilXML.formatXML;

@Component
public class GuiaRemisionTemplate extends TemplateSunat {

    public String buildGuiaRemision(GuiaRemision guiaRemision) throws TemplateException {

        String stringXMLGenerate = null;
        Document document;
        Element elementRoot;

        try {
            Signature signature = buildSignature(
                    guiaRemision.getDenominacionRemitente(),
                    guiaRemision.getNumeroDocumentoIdentidadRemitente()
            );
            document = createDocument();
            elementRoot = addElementRoot(document, ROOT_DESPATCH_ADVICE, getAttributesElementRoot());

            appendChild(document, elementRoot, "ext:UBLExtensions");
            appendChild(document, elementRoot, "cbc:UBLVersionID", ConstantesSunat.UBL_VERSION_2_1);
            appendChild(document, elementRoot, "cbc:CustomizationID", ConstantesSunat.CUSTOMIZATION_VERSION_1_0);

            appendChild(document, elementRoot, "cbc:ID", guiaRemision.getSerie() + "-" + guiaRemision.getNumero());
            appendChild(document, elementRoot, "cbc:IssueDate", guiaRemision.getFechaEmision());
            if (StringUtils.isNotBlank(guiaRemision.getHoraEmision())) {
                appendChild(document, elementRoot, "cbc:IssueTime", guiaRemision.getHoraEmision());
            }
            appendChild(document, elementRoot, "cbc:DespatchAdviceTypeCode", guiaRemision.getTipoComprobante());

            if (guiaRemision.getObservaciones() != null && !guiaRemision.getObservaciones().isEmpty()) {
                for (String obs : guiaRemision.getObservaciones()) {
                    appendChild(document, elementRoot, "cbc:Note", obs);
                }
            }

            appendChildGuiaRemisionDadaBaja(
                    document,
                    elementRoot,
                    guiaRemision.getSerieGuiaBaja(),
                    guiaRemision.getNumeroGuiaBaja(),
                    guiaRemision.getTipoComprobanteBaja(),
                    guiaRemision.getDescripcionComprobanteBaja());

            appendChildDocumentosRelacionados(document, elementRoot, guiaRemision.getNumeracionDAM(), ConstantesSunat.NUMERACION_DAM);
            appendChildDocumentosRelacionados(document, elementRoot, guiaRemision.getNumeracionManifiestoCarga(), ConstantesSunat.NUMERO_MANIFIESTO_CARGA);
            appendChildDocumentosRelacionados(document, elementRoot, guiaRemision.getIdentificadorDocumentoRelacionado(), guiaRemision.getCodigoTipoDocumentoRelacionado());

            appendChildSignature(document, elementRoot, signature);

            appendChildDatosRemitente(
                    document,
                    elementRoot,
                    guiaRemision.getNumeroDocumentoIdentidadRemitente(),
                    guiaRemision.getTipoDocumentoIdentidadRemitente(),
                    guiaRemision.getDenominacionRemitente());

            appendChildDatosDestinatario(
                    document,
                    elementRoot,
                    guiaRemision.getNumeroDocumentoIdentidadDestinatario(),
                    guiaRemision.getTipoDocumentoIdentidadDestinatario(),
                    guiaRemision.getDenominacionDestinatario());

            appendChildDatosProveedor(
                    document,
                    elementRoot,
                    guiaRemision.getNumeroDocumentoIdentidadProveedor(),
                    guiaRemision.getTipoDocumentoIdentidadProveedor(),
                    guiaRemision.getDenominacionProveedor());

            Element shipmentElement = appendChild(document, elementRoot, "cac:Shipment");
            appendChildDatosEnvio(
                    document,
                    shipmentElement,
                    guiaRemision.getMotivoTraslado(),
                    guiaRemision.getDescripcionMotivoTraslado(),
                    guiaRemision.getIndicadorTransbordoProgramado(),
                    guiaRemision.getPesoTotalBrutoBienes(),
                    guiaRemision.getUnidadMedidaPesoBruto(),
                    guiaRemision.getNumeroBultos());

            appendChildTramosTraslado(document, shipmentElement, guiaRemision.getTramosTraslados());

            if (StringUtils.isNotBlank(guiaRemision.getUbigeoPuntoLlegada()) &&
                    StringUtils.isNotBlank(guiaRemision.getDireccionPuntoLlegada())) {

                Element deliveryElement = appendChild(document, shipmentElement, "cac:Delivery");
                Element deliveryAddressElement = appendChild(document, deliveryElement, "cac:DeliveryAddress");
                appendChild(document, deliveryAddressElement, "cbc:ID", guiaRemision.getUbigeoPuntoLlegada());
                appendChild(document, deliveryAddressElement, "cbc:StreetName", guiaRemision.getDireccionPuntoLlegada());
            }

            if (StringUtils.isNotBlank(guiaRemision.getNumeroContenedor())) {

                Element transportHandlingUnitElement = appendChild(document, shipmentElement,
                        "cac:TransportHandlingUnit");
                Element transportEquipmentElement = appendChild(document, transportHandlingUnitElement,
                        "cac:TransportEquipment");
                appendChild(document, transportEquipmentElement, "cbc:ID", guiaRemision.getNumeroContenedor());
            }

            if (StringUtils.isNotBlank(guiaRemision.getUbigeoPuntoPartida()) &&
                    StringUtils.isNotBlank(guiaRemision.getDireccionPuntoPartida())) {

                Element originAddressElement = appendChild(document, shipmentElement, "cac:OriginAddress");
                appendChild(document, originAddressElement, "cbc:ID", guiaRemision.getUbigeoPuntoPartida());
                appendChild(document, originAddressElement, "cbc:StreetName", guiaRemision.getDireccionPuntoPartida());

            }

            if (StringUtils.isNotBlank(guiaRemision.getCodigoPuerto())) {

                Element firstArrivalPortLocationElement = appendChild(document, shipmentElement, "cac:FirstArrivalPortLocation");
                appendChild(document, firstArrivalPortLocationElement, "cbc:ID", guiaRemision.getCodigoPuerto());
            }

            appendBienesToTransportar(document, elementRoot, guiaRemision.getBienesToTransportar());

            stringXMLGenerate = formatXML(buildStringFromDOM(document));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringXMLGenerate;
    }

    private void appendBienesToTransportar(Document document, Element elementRoot, List<GuiaItem> bienesToTransportar) {

        Integer correlativo = 1;
        for (GuiaItem item : bienesToTransportar) {

            Element despatchLineElement = appendChild(document, elementRoot, "cac:DespatchLine");
            appendChild(document, despatchLineElement, "cbc:ID", correlativo);
            appendChild(document, despatchLineElement, "cbc:DeliveredQuantity", item.getCantidad()).
                    setAttribute(ConstantesSunat.ATTRIBUTE_UNIT_CODE, item.getUnidadMedida());
            Element orderLineReferenceElement = appendChild(document, despatchLineElement, "cac:OrderLineReference");
            appendChild(document, orderLineReferenceElement, "cbc:LineID", correlativo);
            Element itemElement = appendChild(document, despatchLineElement, "cac:Item");
            appendChild(document, itemElement, "cbc:Name", item.getDescripcion());
            if (StringUtils.isNotBlank(item.getCodigoItem())) {

                Element sellersItemIdentificationElement = appendChild(document, itemElement,
                        "cac:SellersItemIdentification");
                appendChild(document, sellersItemIdentificationElement, "cbc:ID", item.getCodigoItem());
            }
            item.setNumeroOrden(correlativo);

            correlativo++;
        }
    }

    private void appendChildTramosTraslado(Document document, Element shipmentElement,
                                           List<TramoTraslado> tramosTraslados) {

        Integer correlativo = 1;
        for (TramoTraslado tramo : tramosTraslados) {

            Element shipmentStageElement = appendChild(document, shipmentElement, "cac:ShipmentStage");
            appendChild(document, shipmentStageElement, "cbc:ID", correlativo);
            appendChild(document, shipmentStageElement, "cbc:TransportModeCode",
                    tramo.getModalidadTraslado());
            Element transitPeriodElement = appendChild(document, shipmentStageElement, "cac:TransitPeriod");
            appendChild(document, transitPeriodElement, "cbc:StartDate", tramo.getFechaInicioTraslado());

            if (StringUtils.isNotBlank(tramo.getNumeroDocumentoIdentidadTransportista()) &&
                    StringUtils.isNotBlank(tramo.getTipoDocumentoIdentidadTransportista()) &&
                    StringUtils.isNotBlank(tramo.getDenominacionTransportista())) {

                Element carrierPartyElement = appendChild(document, shipmentStageElement, "cac:CarrierParty");
                Element partyIdentificationElement = appendChild(document, carrierPartyElement, "cac:PartyIdentification");

                appendChild(document, partyIdentificationElement, "cbc:ID", tramo.getNumeroDocumentoIdentidadTransportista())
                        .setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID, tramo.getTipoDocumentoIdentidadTransportista());

                Element partyNameElement = appendChild(document, carrierPartyElement, "cac:PartyName");
                appendChild(document, partyNameElement, "cbc:Name", tramo.getDenominacionTransportista());
            }

            if (StringUtils.isNotBlank(tramo.getNumeroPlacaVehiculo())) {

                Element transportMeansElement = appendChild(document, shipmentStageElement, "cac:TransportMeans");
                Element roadTransportElement = appendChild(document, transportMeansElement, "cac:RoadTransport");
                appendChild(document, roadTransportElement, "cbc:LicensePlateID", tramo.getNumeroPlacaVehiculo());
            }

            if (StringUtils.isNotBlank(tramo.getNumeroDocumentoIdentidadConductor())
                    && StringUtils.isNotBlank(tramo.getTipoDocumentoIdentidadConductor())) {

                Element driverPersonElement = appendChild(document, shipmentStageElement, "cac:DriverPerson");
                appendChild(document, driverPersonElement, "cbc:ID", tramo.getNumeroDocumentoIdentidadConductor()).
                        setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID, tramo.getTipoDocumentoIdentidadConductor());
            }
            tramo.setCorrelativoTramo(correlativo);

            correlativo++;
        }
    }

    private void appendChildDatosRemitente(Document document, Element elementRoot,
                                           String numeroDocumentoIdentidadRemitente, String tipoDocumentoIdentidadRemitente,
                                           String denominacionRemitente) {

        Element supplierPartyElement = appendChild(document, elementRoot, "cac:DespatchSupplierParty");
        appendChild(document, supplierPartyElement, "cbc:CustomerAssignedAccountID",
                numeroDocumentoIdentidadRemitente).setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID,
                tipoDocumentoIdentidadRemitente);
        Element partyElement = appendChild(document, supplierPartyElement, "cac:Party");
        Element partyLegalEntityElement = appendChild(document, partyElement, "cac:PartyLegalEntity");
        appendChild(document, partyLegalEntityElement, "cbc:RegistrationName", denominacionRemitente);

    }

    private void appendChildDatosDestinatario(Document document, Element elementRoot,
                                              String numeroDocumentoIdentidadDestinatario, String tipoDocumentoIdentidadDestinatario,
                                              String denominacionDestinatario) {

        Element customerPartyElement = appendChild(document, elementRoot, "cac:DeliveryCustomerParty");
        appendChild(document, customerPartyElement, "cbc:CustomerAssignedAccountID",
                numeroDocumentoIdentidadDestinatario).setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID,
                tipoDocumentoIdentidadDestinatario);
        Element partyElement = appendChild(document, customerPartyElement, "cac:Party");
        Element partyLegalEntityElement = appendChild(document, partyElement, "cac:PartyLegalEntity");
        appendChild(document, partyLegalEntityElement, "cbc:RegistrationName", denominacionDestinatario);

    }

    private void appendChildDatosProveedor(Document document, Element elementRoot,
                                           String numeroDocumentoIdentidadTercero, String tipoDocumentoIdentidadTercero, String denominacionTercero) {
        if (tipoDocumentoIdentidadTercero != null) {
            Element sellerPartyElement = appendChild(document, elementRoot, "cac:SellerSupplierParty");
            appendChild(document, sellerPartyElement, "cbc:CustomerAssignedAccountID",
                    numeroDocumentoIdentidadTercero).setAttribute(ConstantesSunat.ATTRIBUTE_SCHEME_ID,
                    tipoDocumentoIdentidadTercero);
            Element partyElement = appendChild(document, sellerPartyElement, "cac:Party");
            Element partyLegalEntityElement = appendChild(document, partyElement, "cac:PartyLegalEntity");
            appendChild(document, partyLegalEntityElement, "cbc:RegistrationName", denominacionTercero);
        }
    }

    private void appendChildDocumentosRelacionados(Document document, Element elementRoot,
                                                   String numeroDocumentoRelacionado, String tipoDocumentoRelacionado) {

        if (StringUtils.isNotBlank(numeroDocumentoRelacionado) && StringUtils.isNotBlank(tipoDocumentoRelacionado)) {

            Element additionalDocumentReferenceElement = appendChild(document, elementRoot, "cac:AdditionalDocumentReference");
            appendChild(document, additionalDocumentReferenceElement, "cbc:ID", numeroDocumentoRelacionado);
            appendChild(document, additionalDocumentReferenceElement, "cbc:DocumentTypeCode", tipoDocumentoRelacionado);
        }

    }

    private void appendChildDatosEnvio(Document document, Element shipmentElement, String motivoTraslado,
                                       String descripcionMotivoTraslado, Boolean indicadorTransbordoProgramado, BigDecimal pesoTotalBrutoBienes,
                                       String unidadMedidaPesoBruto, Long numeroBultos) {

        appendChild(document, shipmentElement, "cbc:ID", motivoTraslado);
        appendChild(document, shipmentElement, "cbc:HandlingCode", motivoTraslado);
        if (StringUtils.isNotBlank(descripcionMotivoTraslado)) {
            appendChild(document, shipmentElement, "cbc:Information", descripcionMotivoTraslado);
        }
        appendChild(document, shipmentElement, "cbc:GrossWeightMeasure", pesoTotalBrutoBienes).
                setAttribute(ConstantesSunat.ATTRIBUTE_UNIT_CODE, unidadMedidaPesoBruto);
        if (numeroBultos != null) {
            appendChild(document, shipmentElement, "cbc:TotalTransportHandlingUnitQuantity", numeroBultos);
        }
        appendChild(document, shipmentElement, "cbc:SplitConsignmentIndicator", indicadorTransbordoProgramado);
    }

    private void appendChildGuiaRemisionDadaBaja(
            Document document, Element elementRoot,
            String serieBaja, Integer numeroBaja,
            String tipoComprobanteBaja,
            String descripcionTipoComprobanteBaja) {

        boolean existeDatosBaja = (StringUtils.isNotBlank(serieBaja) && numeroBaja != null && numeroBaja > 0) ? true : false;
        if (existeDatosBaja) {

            Element orderReferenceElement = appendChild(document, elementRoot, "cac:OrderReference");
            appendChild(document, orderReferenceElement, "cbc:ID", serieBaja + "-" + numeroBaja);
            Element orderTypeCodeElement = appendChild(document, orderReferenceElement, "cbc:OrderTypeCode",
                    tipoComprobanteBaja);

            if (StringUtils.isNotBlank(descripcionTipoComprobanteBaja)) {
                orderTypeCodeElement.setAttribute(ConstantesSunat.ATTRIBUTE_NAME, descripcionTipoComprobanteBaja);
            }
        }
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

        atributos.put("xmlns", "urn:oasis:names:specification:ubl:schema:xsd:DespatchAdvice-2");
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
