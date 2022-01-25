package pe.com.certifakt.apifact.template;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.dto.GetStatusCdrDTO;

import static pe.com.certifakt.apifact.util.UtilXML.formatXML;

@Component
public class RequestSunatOseTemplate {

    @Value("${sunat.rucPse}")
    private String rucPseValue;

    @Value("${sunat.usuarioPse}")
    private String usuarioPseValue;

    @Value("${sunat.clavePse}")
    private String clavePseValue;


    public String buildSendSummary(String fileName, String contentFileBase64) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String username = usuarioPseValue;
            String password = clavePseValue;
            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");
            xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append(password).append("</wsse:Password>\n");
            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:sendSummary>\n");
            xml.append("<fileName>").append(fileName).append("</fileName>\n");
            xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
            xml.append("</ser:sendSummary>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildSendSummary " : ex.getMessage()) + " en el método buildGetStatus");
        }
        return formatXML(xml.toString());
    }

    public String buildSendBill(String fileName, String contentFileBase64) {
       
    	StringBuilder xml = new StringBuilder();
        String ruc = rucPseValue;
        String username = usuarioPseValue;
        String password = clavePseValue;
        xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
        xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
        xml.append("<soapenv:Header>\n");
        xml.append("<wsse:Security>\n");
        xml.append("<wsse:UsernameToken>\n");
        xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
        xml.append("<wsse:Password>").append(password).append("</wsse:Password>\n");
        xml.append("</wsse:UsernameToken>\n");
        xml.append("</wsse:Security>\n");
        xml.append("</soapenv:Header>\n");
        xml.append("<soapenv:Body>\n");
        xml.append("<ser:sendBill>\n");
        xml.append("<fileName>").append(fileName).append("</fileName>\n");
        xml.append("<contentFile>").append(contentFileBase64).append("</contentFile>\n");
        xml.append("</ser:sendBill>\n");
        xml.append("</soapenv:Body>\n");
        xml.append("</soapenv:Envelope>\n");
        
        return formatXML(xml.toString());
    }
    
    public String buildGetStatusCDR(GetStatusCdrDTO dataGetStatus) {
    	
    	StringBuilder xml = new StringBuilder();
    	String ruc = rucPseValue;
    	String username = usuarioPseValue;
    	String password = clavePseValue;
    	
    	xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
    	xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
    	xml.append("<soapenv:Header>\n");
    	xml.append("<wsse:Security>\n");
    	xml.append("<wsse:UsernameToken>\n");
    	xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
    	xml.append("<wsse:Password>").append(password).append("</wsse:Password>\n");
    	xml.append("</wsse:UsernameToken>\n");
    	xml.append("</wsse:Security>\n");
    	xml.append("</soapenv:Header>\n");
    	xml.append("<soapenv:Body>\n");
    	xml.append("<ser:getStatusCdr>\n");
    	xml.append("<rucComprobante>").append(dataGetStatus.getRuc()).append("</rucComprobante>\n");
    	xml.append("<tipoComprobante>").append(dataGetStatus.getTipoComprobante()).append("</tipoComprobante>\n");
    	xml.append("<serieComprobante>").append(dataGetStatus.getSerie()).append("</serieComprobante>\n");
    	xml.append("<numeroComprobante>").append(dataGetStatus.getNumero()).append("</numeroComprobante>\n");
    	xml.append("</ser:getStatusCdr>\n");
    	xml.append("</soapenv:Body>\n");
    	xml.append("</soapenv:Envelope>\n");
    	
    	return formatXML(xml.toString());
    }

    public String buildGetStatus(String nroTicket) {
        StringBuilder xml = new StringBuilder();
        try {
            String ruc = rucPseValue;
            String username = usuarioPseValue;
            String password = clavePseValue;
            xml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n");
            xml.append("xmlns:ser=\"http://service.sunat.gob.pe\" xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n");
            xml.append("<soapenv:Header>\n");
            xml.append("<wsse:Security>\n");
            xml.append("<wsse:UsernameToken>\n");
            xml.append("<wsse:Username>").append(ruc).append(username).append("</wsse:Username>\n");
            xml.append("<wsse:Password>").append(password).append("</wsse:Password>\n");
            xml.append("</wsse:UsernameToken>\n");
            xml.append("</wsse:Security>\n");
            xml.append("</soapenv:Header>\n");
            xml.append("<soapenv:Body>\n");
            xml.append("<ser:getStatus>\n");
            xml.append("<ticket>").append(nroTicket).append("</ticket>\n");
            xml.append("</ser:getStatus>\n");
            xml.append("</soapenv:Body>\n");
            xml.append("</soapenv:Envelope>\n");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println((ex.getMessage() == null ? "ERROR en buildGetStatus" : ex.getMessage()) + " en el método buildGetStatus");
        }
        return formatXML(xml.toString());
    }
}
