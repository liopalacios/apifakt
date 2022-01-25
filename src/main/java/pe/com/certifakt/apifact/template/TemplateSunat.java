package pe.com.certifakt.apifact.template;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pe.com.certifakt.apifact.bean.Signature;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import static pe.com.certifakt.apifact.util.UtilXML.appendChild;

public class TemplateSunat {

    protected final static String ROOT_VOIDED_DOCUMENTS = "VoidedDocuments";
    protected final static String ROOT_CREDIT_NOTE = "CreditNote";
    protected final static String ROOT_DEBIT_NOTE = "DebitNote";
    protected final static String ROOT_RETENTION = "Retention";
    protected final static String ROOT_PERCEPTION = "Perception";
    protected final static String ROOT_DESPATCH_ADVICE = "DespatchAdvice";

    protected static Document createDocument()
            throws ParserConfigurationException {

        DocumentBuilder documentBuilder;
        Document document = null;

        documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = documentBuilder.newDocument();

        return document;
    }

    protected Element addElementRoot(Document document, String nombreElementRoot,
                                     Map<String, String> attributesElementRoot) {

        Element elementRoot;

        elementRoot = document.createElement(nombreElementRoot);
        document.appendChild(elementRoot);

        for (Entry<String, String> entry : attributesElementRoot.entrySet()) {

            Attr atributo = document.createAttribute(entry.getKey());
            atributo.setValue(entry.getValue());
            elementRoot.setAttributeNode(atributo);
        }

        return elementRoot;
    }

    protected String buildStringFromDOM(Document document)
            throws TransformerFactoryConfigurationError, TransformerException {

        String stringBuild = null;
        StreamResult streamResult;
        StringWriter stringWriter;
        Transformer transformer;
        DOMSource domSource;

        domSource = new DOMSource(document);
        stringWriter = new StringWriter();
        streamResult = new StreamResult(stringWriter);
        transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(domSource, streamResult);
        stringBuild = stringWriter.toString();

        return stringBuild;

    }

    protected void appendChildSignature(Document document, Element elementRoot,
                                        Signature signature) {

        Element signatureElement = appendChild(document, elementRoot, "cac:Signature");

        appendChild(document, signatureElement, "cbc:ID", signature.getId());

        Element signatoryParty = appendChild(document, signatureElement, "cac:SignatoryParty");
        Element partyIdentification = appendChild(document, signatoryParty, "cac:PartyIdentification");
        appendChild(document, partyIdentification, "cbc:ID", signature.getRucEmisor());

        Element cacPartyName = appendChild(document, signatoryParty, "cac:PartyName");
        appendChild(document, cacPartyName, "cbc:Name", signature.getDenominacionEmisor());

        Element digitalSignatureAttachment = appendChild(document, signatureElement, "cac:DigitalSignatureAttachment");
        Element externalReference = appendChild(document, digitalSignatureAttachment, "cac:ExternalReference");
        appendChild(document, externalReference, "cbc:URI", signature.getUri());

    }
}
