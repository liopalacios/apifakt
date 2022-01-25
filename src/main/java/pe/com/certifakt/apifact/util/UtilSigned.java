package pe.com.certifakt.apifact.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UtilSigned {

    public static void outputDocToOutputStream(Document doc, ByteArrayOutputStream signatureFile) throws TransformerException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "no");
        transformer.setOutputProperty("encoding", "UTF-8");
        transformer.transform(new DOMSource(doc), new StreamResult(signatureFile));
    }

    public static Document buildDocument(InputStream inDocument) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStreamReader reader = new InputStreamReader(inDocument, "UTF-8");//ISO8859_1
        Document doc = db.parse(new InputSource(reader));
        return doc;
    }

    public static Node addExtensionContent(Document doc) {
        NodeList nodeList = doc.getDocumentElement().getElementsByTagNameNS("urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2", "UBLExtensions");
        Node extensions = nodeList.item(0);
        extensions.appendChild(doc.createTextNode("\t"));
        Element extension = doc.createElement("ext:UBLExtension");
        extension.appendChild(doc.createTextNode("\n\t"));
        Element content = doc.createElement("ext:ExtensionContent");
        extension.appendChild(content);
        extension.appendChild(doc.createTextNode("\n\t"));
        extensions.appendChild(extension);
        extensions.appendChild(doc.createTextNode("\n"));
        return content;
    }

    public static String getNode(Node node) throws Exception {
        StringBuilder valorClave = new StringBuilder();
        valorClave.setLength(0);
        Integer tamano = node.getChildNodes().getLength();
        int i = 0;
        while (i < tamano) {
            Node c = node.getChildNodes().item(i);
            if (c.getNodeType() == 3) {
                valorClave.append(c.getNodeValue());
            }
            ++i;
        }
        String nodo = valorClave.toString().trim();
        return nodo;
    }

}
