package pe.com.certifakt.apifact.util;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


public class RebuildFile {

	/*
	public static Map<String, String> getDatosFromCDRToGetStatus(String base64) throws IOException{
		
		Map<String, String> resultDatos;
    	
    	String tipoDocumento;
    	String rucEmisor;
    	String nameFile;
    	
    	resultDatos = getDataResponseFromCDR(base64);
    	nameFile = resultDatos.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
    	
    	tipoDocumento = nameFile.substring(14, 16);
    	rucEmisor = nameFile.substring(2, 13);
		
        resultDatos.put(ConstantesParameter.PARAM_TIPO_ARCHIVO, tipoDocumento);
        resultDatos.put(ConstantesParameter.PARAM_RUC_EMISOR, rucEmisor);
        
		return resultDatos;
	}
	*/
	
	public static Map<String, String> getDataResponseFromCDR(String base64) throws IOException{
		
		Map<String, String> resultDatos;
		NodeList nodeResponseCode;
		NodeList nodeDescription;
    	String responseCode;
    	String description;
    	String nameFile;
    	String tipoDocumento;
    	String rucEmisor;
    	String formatXML;
    	Document document;
    	String[] datosFileXml;
    	File fileZip;
    	
    	fileZip = UtilConversion.decodeBase64ToFile(base64, ConstantesParameter.TYPE_FILE_ZIP);
    	datosFileXml = getDataFileXmlAsStringFromFileZip(fileZip);
    	formatXML = datosFileXml[0];
    	nameFile  = datosFileXml[1];
    	
        document = UtilXML.parseXmlFile(formatXML);
        nodeDescription = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_DESCRIPTION);
        nodeResponseCode = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_RESPONSE_CODE);

		if (nodeDescription.getLength() == 0 && nodeResponseCode.getLength() == 0){
			nodeDescription = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_DESCRIPTION_OSE);
			nodeResponseCode = document.getElementsByTagName(ConstantesSunat.ATTRIBUTE_TAG_CBC_RESPONSE_CODE_OSE);
		}

        List<String> codigosRpta = new ArrayList<String>();
        List<String> descripcionesRpta = new ArrayList<String>();
        
        for(int i = 0; i < nodeResponseCode.getLength(); i++ ) {
        	codigosRpta.add(nodeResponseCode.item(i).getTextContent());
        	descripcionesRpta.add(nodeDescription.item(i).getTextContent());
        }
        nameFile = nameFile.substring(0, (nameFile.length()-4));
        tipoDocumento = nameFile.substring(14, 16);
    	rucEmisor = nameFile.substring(2, 13);
    	
        responseCode = String.join("|", codigosRpta);
        description = String.join("|", descripcionesRpta);
        
        resultDatos = new HashMap<>();
        resultDatos.put(ConstantesParameter.PARAM_NAME_DOCUMENT, nameFile);
        resultDatos.put(ConstantesParameter.PARAM_DESCRIPTION, description);
        resultDatos.put(ConstantesParameter.PARAM_RESPONSE_CODE, responseCode);
        resultDatos.put(ConstantesParameter.PARAM_TIPO_ARCHIVO, tipoDocumento);
        resultDatos.put(ConstantesParameter.PARAM_RUC_EMISOR, rucEmisor);
        
		return resultDatos;
	}
	
	@SuppressWarnings("unchecked")
	private static String[] getDataFileXmlAsStringFromFileZip(File fileZip) 
			throws ZipException, IOException {
		
		InputStream inputStream = null;
		String[] resp = new String[2];
		StringBuilder stringBuilder;
		String nameFile = null;
		ZipEntry zipEntry;
		String inputLine;
		String formatXML;
		ZipFile zip;
		
		zip = new ZipFile(fileZip);
		
		Enumeration<ZipEntry> enumeration = (Enumeration<ZipEntry>) zip.entries();
		while (enumeration.hasMoreElements()) {
		
			zipEntry = enumeration.nextElement();
			nameFile = zipEntry.getName();
			
			if(nameFile.toLowerCase().endsWith(ConstantesParameter.TYPE_FILE_XML)) {
				
				inputStream = zip.getInputStream(zipEntry);
				break;
			}
		}
		
		stringBuilder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        while ((inputLine = in.readLine()) != null) {
        	stringBuilder.append(inputLine);
        }
        
        inputStream.close();
        zip.close();
        
        formatXML = UtilXML.formatXML(stringBuilder.toString());
        
        resp[0] = formatXML;
        resp[1] = nameFile;
        
		return resp;
	}	
	
}
