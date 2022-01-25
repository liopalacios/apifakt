package pe.com.certifakt.apifact.service.impl;

import io.sentry.spring.SentryExceptionResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pe.com.certifakt.apifact.bean.ResponseServer;
import pe.com.certifakt.apifact.bean.ResponseSunat;
import pe.com.certifakt.apifact.dto.GetStatusCdrDTO;
import pe.com.certifakt.apifact.dto.PaypalTransaction;
import pe.com.certifakt.apifact.dto.inter.OseInterDto;
import pe.com.certifakt.apifact.enums.*;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.ErrorEntity;
import pe.com.certifakt.apifact.model.OsesEntity;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.ErrorRepository;
import pe.com.certifakt.apifact.service.CompanyService;
import pe.com.certifakt.apifact.service.OsesService;
import pe.com.certifakt.apifact.service.SendSunatService;
import pe.com.certifakt.apifact.template.RequestSunatTemplate;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.Logger;
import pe.com.certifakt.apifact.util.RebuildFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pe.com.certifakt.apifact.util.UtilXML.formatXML;
import static pe.com.certifakt.apifact.util.UtilXML.parseXmlFile;

@Service
@Slf4j
public class SendSunatServiceImpl implements SendSunatService {

    @Autowired
    private RequestSunatTemplate requestSunatTemplate;
    @Autowired
    private ErrorRepository errorRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private OsesService osesService;
    @Autowired
    private CompanyRepository companyRepository;
    @Value("${sunat.endpoint}")
    private String endPointSunat;
    @Value("${sunat.endpointOtrosCpe}")
    private String endPointSunatOtrosCpe;
    @Value("${sunat.endpointGuiaRemision}")
    private String endPointSunatGuiaRemision;
    @Value("${sunat.endpointConsultaCDR}")
    private String endPointConsultaCDR;

    private String endPoint="";

    final String baseurlget =
            "http://200.41.86.242:8077/api/sendose?endpoint=https://osetesting.bizlinks.com.pe/ol-ti-itcpe/billService&tagOperacionOK=ticket";
    final String baseurlgetContent =
            "http://200.41.86.242:8077/api/sendose?endpoint=https://osetesting.bizlinks.com.pe/ol-ti-itcpe/billService&tagOperacionOK=content";
    final String baseurlgetApply =
            "http://200.41.86.242:8077/api/sendose?endpoint=https://osetesting.bizlinks.com.pe/ol-ti-itcpe/billService&tagOperacionOK=applicationResponse";
    final String baseurlgetGuiaApply =
            "http://200.41.86.242:8077/api/sendguiaose?endpoint=https://osetesting.bizlinks.com.pe/ol-ti-itcpe/billService&tagOperacionOK=applicationResponse";
    @Override
    public ResponseSunat getStatus(String nroTicket, String tipoResumen, String rucEmisor) {

        ResponseSunat responseSunat = new ResponseSunat();
        String formatSoap;
        try {
            formatSoap = getFormatGetStatus(rucEmisor,nroTicket);
            /*ResponseServer responseServer = send(
                    formatSoap,
                    obtenerEndPointSunat(rucEmisor),
                    ConstantesParameter.TAG_GET_STATUS_CONTENT
            );*/
            ResponseServer responseServer = null;
            OseInterDto ose = companyRepository.findOseByRucInter(rucEmisor);
            if (ose != null) {
                if (ose.getId()==1){
                    responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                            ConstantesParameter.TAG_GET_STATUS_CONTENT);
                }else if (ose.getId()==2){
                    RestTemplate template = new RestTemplate();
                    URI uriget = new URI(ose.getUrlfacturas()+ConstantesParameter.TAG_GET_STATUS_CONTENT);
                    HttpHeaders requestHeaders = new HttpHeaders();
                    HttpEntity<String> requestEntity = new HttpEntity<>(formatSoap, requestHeaders);
                    ResponseEntity<ResponseServer> entity = template.exchange(uriget, HttpMethod.POST, requestEntity, ResponseServer.class);
                    System.out.println("PUENTE OSE BLIZ");
                    System.out.println(entity);
                    if(entity.getStatusCode() == HttpStatus.OK){
                        responseServer = entity.getBody();
                        System.out.println("RESULTADO CONSULTA TICKET ");
                    }
                }
            } else {
                responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                        ConstantesParameter.TAG_GET_STATUS_CONTENT);
            }



            OperacionLogEnum operLog = (tipoResumen.equals(ConstantesSunat.RESUMEN_DIARIO_BOLETAS)) ?
                    OperacionLogEnum.STATUS_SUNAT_SUMMARY : OperacionLogEnum.STATUS_SUNAT_VOIDED;
            Logger.register(TipoLogEnum.INFO, rucEmisor, nroTicket,
                    operLog, SubOperacionLogEnum.SEND_SUNAT, "{" + ConstantesParameter.MSG_RESP_SUB_PROCESO_OK +
                            "}{" + responseServer.toString() + "}");
            buildResponseSendBillStatus(
                    responseSunat,
                    responseServer,
                    ConstantesParameter.TAG_GET_STATUS_CONTENT
            );
        } catch (IOException e) {

            responseSunat.setMessage("Error al comunicarse con la Sunat." + e.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.ERROR_INTERNO_WS_API);
        } catch (Exception ex) {
            responseSunat.setMessage(ex.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.ERROR_INTERNO_WS_API);
        }
        return responseSunat;
    }

    @Override
    public ResponseSunat sendSummary(String fileName, String contentFileBase64, String rucEmisor) {

        ResponseSunat responseSunat = new ResponseSunat();
        String formatSoap;
        Document document;
        NodeList nodeFaultcode;
        NodeList nodeFaultstring;
        Node node;

        try {

            formatSoap = obtenerFormatBuildSendSumary(rucEmisor,fileName, contentFileBase64);

            /*ResponseServer responseServer = send(
                    formatSoap,
                    obtenerEndPointSunat(rucEmisor),
                    ConstantesParameter.TAG_SEND_SUMMARY_TICKET
            );*/
            ResponseServer responseServer = null;
            OseInterDto ose = companyRepository.findOseByRucInter(rucEmisor);
            if (ose != null) {
                if (ose.getId()==1){
                    responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                            ConstantesParameter.TAG_SEND_SUMMARY_TICKET);
                }else if (ose.getId()==2) {
                    RestTemplate template = new RestTemplate();
                    URI uriget = new URI(ose.getUrlfacturas()+ConstantesParameter.TAG_SEND_SUMMARY_TICKET);
                    HttpHeaders requestHeaders = new HttpHeaders();
                    HttpEntity<String> requestEntity = new HttpEntity<>(formatSoap, requestHeaders);
                    ResponseEntity<ResponseServer> entity = template.exchange(uriget, HttpMethod.POST, requestEntity, ResponseServer.class);
                    System.out.println("PUENTE OSE BLIZ");
                    System.out.println(entity);
                    if (entity.getStatusCode() == HttpStatus.OK) {
                        responseServer = entity.getBody();
                        System.out.println("user response retrieved ");
                    }
                }
            } else {
                responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                        ConstantesParameter.TAG_SEND_SUMMARY_TICKET);
            }
            document = parseXmlFile(responseServer.getContent());

            if (responseServer.isSuccess()) {

                NodeList nodeTicket = document.getElementsByTagName(ConstantesParameter.TAG_SEND_SUMMARY_TICKET);
                String valueTicket = nodeTicket.item(0).getTextContent();
                responseSunat.setSuccess(true);
                responseSunat.setTicket(valueTicket);
                responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS);
            } else {

                String valueFaultcode = null;
                String valueFaultstring = null;

                nodeFaultcode = document.getElementsByTagName("faultcode");
                nodeFaultstring = document.getElementsByTagName("faultstring");
                node = nodeFaultcode.item(0);

                if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                    valueFaultcode = (node.getTextContent()).replaceAll("[^0-9]", "");
                    valueFaultstring = nodeFaultstring.item(0).getTextContent();
                    if (valueFaultcode.equals("")) {
                        responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.WITHOUT_CONNECTION);
                    } else {
                        responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                    }
                    responseSunat.setStatusCode(valueFaultcode);
                    responseSunat.setMessage(valueFaultstring);
                }
                responseSunat.setSuccess(false);

            }
        } catch (IOException e) {
            responseSunat.setMessage("Error al comunicarse con la Sunat." + e.getMessage());
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.ERROR_INTERNO_WS_API);
            responseSunat.setSuccess(false);
        } catch (Exception ex) {

            responseSunat.setMessage(ex.getMessage());
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.ERROR_INTERNO_WS_API);
            responseSunat.setSuccess(false);
        }
        return responseSunat;
    }

    @Override
    public ResponseSunat getStatusCDR(GetStatusCdrDTO statusDto, String rucEmisor) {

        ResponseSunat responseSunat = new ResponseSunat();
        try {

            String formatSoap = obtenerStatusCdr(statusDto,rucEmisor);

            ResponseServer responseServer = null;
            OseInterDto ose = companyRepository.findOseByRucInter(rucEmisor);
            if (ose != null) {
                if (ose.getId()==1) {
                    responseServer = send(
                            formatSoap,
                            obtenerEndPointConsultaCdr(rucEmisor),
                            ConstantesParameter.TAG_GET_STATUS_CONTENT
                    );
                }else if (ose.getId()==2) {
                    RestTemplate template = new RestTemplate();
                    URI uriget = new URI(ose.getUrlfacturas()+ConstantesParameter.TAG_GET_STATUS_CONTENT);
                    HttpHeaders requestHeaders = new HttpHeaders();
                    HttpEntity<String> requestEntity = new HttpEntity<>(formatSoap, requestHeaders);
                    ResponseEntity<ResponseServer> entity = template.exchange(uriget, HttpMethod.POST, requestEntity, ResponseServer.class);
                    System.out.println("PUENTE OSE BLIZ");
                    System.out.println(entity);
                    if (entity.getStatusCode() == HttpStatus.OK) {
                        responseServer = entity.getBody();
                        System.out.println("user response retrieved ");
                    }
                }
            } else {
                responseServer = send(formatSoap, obtenerEndPointConsultaCdr(rucEmisor),
                        ConstantesParameter.TAG_GET_STATUS_CONTENT);
            }
            System.out.println("responseServerCdr: "+responseServer.toString());
            buildResponseGetStatusCDR(responseSunat, responseServer);

        } catch (IOException e) {

            responseSunat.setMessage("Error al comunicarse con la Sunat." + e.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.WITHOUT_CONNECTION);
        } catch (Exception ex) {
            responseSunat.setMessage(ex.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.ERROR_INTERNO_WS_API);
        }

        return responseSunat;
    }

    @Override
    public ResponseSunat sendBillPaymentVoucher(String fileName, String contentFileBase64, String rucEmisor) {

        ResponseSunat responseSunat = new ResponseSunat();

        try {

            String formatSoap = obtenerFormat(rucEmisor,fileName,contentFileBase64);
            System.out.println("PREPARANDO XML");
            System.out.println(formatSoap);
            System.out.println("--------------");

            ResponseServer responseServer = null;
            OseInterDto ose = companyRepository.findOseByRucInter(rucEmisor);
            if (ose != null) {
                if (ose.getId() == 1) {
                    responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                            ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
                }else if (ose.getId()==2) {
                    RestTemplate template = new RestTemplate();
                    URI uriget = new URI(ose.getUrlfacturas()+ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
                    HttpHeaders requestHeaders = new HttpHeaders();
                    HttpEntity<String> requestEntity = new HttpEntity<>(formatSoap, requestHeaders);
                    ResponseEntity<ResponseServer> entity = template.exchange(uriget, HttpMethod.POST, requestEntity, ResponseServer.class);
                    System.out.println("PUENTE OSE BLIZ");
                    System.out.println(entity);
                    if (entity.getStatusCode() == HttpStatus.OK) {
                        responseServer = entity.getBody();
                        System.out.println("user response retrieved ");
                    }
                }
            } else {
                responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                        ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
            }
            log.info("*** CONSUMIENDO  WEB SERVICE - SUNAT ***");

            Logger.register(TipoLogEnum.INFO, fileName.substring(0, 11),
                    fileName.substring(0, fileName.length() - 4), OperacionLogEnum.SEND_SUNAT_VOUCHER,
                    SubOperacionLogEnum.SEND_BILL,
                    "{" + ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "}{" + responseServer.toString() + "}");

            buildResponseSendBillStatus(responseSunat, responseServer, ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);


        } catch (IOException e) {

            responseSunat.setMessage("Error al comunicarse con la Sunat." + e.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.WITHOUT_CONNECTION);

            Logger.register(TipoLogEnum.ERROR, fileName.substring(0, 11), fileName.substring(0, fileName.length() - 4),
                    OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.SEND_BILL,
                    e.getMessage(), "{fileName: " + fileName + "}{contentFileBase64:" + contentFileBase64 + "}", e);
            new SentryExceptionResolver().resolveException(null, null, e, e);

        } catch (Exception ex) {

            responseSunat.setMessage(ex.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.ERROR_INTERNO_WS_API);

            Logger.register(TipoLogEnum.ERROR, fileName.substring(0, 11), fileName.substring(0, fileName.length() - 4),
                    OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.SEND_BILL,
                    ex.getMessage(), "{fileName: " + fileName + "}{contentFileBase64:" + contentFileBase64 + "}", ex);
            new SentryExceptionResolver().resolveException(null, null, ex, ex);
        }

        return responseSunat;
    }



    private String obtenerEndPointSunat(String ruc) {
        OseInterDto ose = companyRepository.findOseByRucInter(ruc);
        if (ose != null) {
            return ose.getUrlfacturas();
        } else {
            return endPointSunat;
        }
    }

    private String obtenerEndPointSunatOtrosCpe(String ruc) {
        OsesEntity ose = companyRepository.findOseByRuc(ruc);
        if (ose != null) {
            return ose.getUrlothercpe();
        } else {
            return endPointSunatOtrosCpe;
        }
    }

    private String obtenerEndPointGuia(String ruc) {
        OseInterDto ose = companyRepository.findOseByRucInter(ruc);
        if (ose != null) {
            return ose.getUrlguias();
        } else {
            return endPointSunatGuiaRemision;
        }
    }

    private String obtenerEndPointConsultaCdr(String ruc) {
        return endPointConsultaCDR;
        /*OsesEntity ose = companyRepository.findOseByRuc(ruc);
        if (ose != null) {
            return ose.getUrlconsultacdr();
        } else {
            return endPointConsultaCDR;
        }*/
    }

    private String getFormatGetStatus(String ruc, String nroTicket) {
        OseInterDto ose = companyRepository.findOseByRucInter(ruc);
        String formato = "";
        if (ose != null) {
            if (ose.getId()==1){
                formato =  requestSunatTemplate.buildGetStatusOse(nroTicket);
            }else if (ose.getId()==2){
                formato =  requestSunatTemplate.buildGetStatusOseBliz(nroTicket);
            }
        } else {
            formato =  requestSunatTemplate.buildGetStatus(nroTicket);
        }
        return formato ;
    }

    private String obtenerFormatBuildSendSumary(String ruc, String fileName, String contentFileBase64) {
        OseInterDto ose = companyRepository.findOseByRucInter(ruc);
        String formato = "";
        if (ose != null) {
            if (ose.getId()==1){
                formato =  requestSunatTemplate.buildOseSendSummary(fileName, contentFileBase64);
            }else if (ose.getId()==2){
                formato =  requestSunatTemplate.buildOseBlizSendSummary(fileName, contentFileBase64);
            }
        } else {
            formato =  requestSunatTemplate.buildSendSummary(fileName, contentFileBase64);
        }
        return formato;
    }

    private String obtenerFormat(String ruc, String fileName, String contentFileBase64) {
        OseInterDto ose = companyRepository.findOseByRucInter(ruc);
        String formato = "";
        if (ose != null) {
            if (ose.getId()==1){
                formato =  requestSunatTemplate.buildSendOseBill(fileName, contentFileBase64);
            }else if (ose.getId()==2){
                formato =  requestSunatTemplate.buildSendOseBlizBill(fileName, contentFileBase64);
            }
        } else {
            formato =  requestSunatTemplate.buildSendBill(fileName, contentFileBase64);
        }
        return formato ;

    }

    private String obtenerBuildSendBill(String ruc, String fileName, String contentFileBase64) {
        OseInterDto ose = companyRepository.findOseByRucInter(ruc);
        String buildsend = "";
        if (ose != null) {
            if (ose.getId()==1){
                buildsend =  requestSunatTemplate.buildSendOseBill(fileName, contentFileBase64);
            }else if (ose.getId()==2){
                buildsend =  requestSunatTemplate.buildSendOseBlizBill(fileName, contentFileBase64);
            }
        } else {
            buildsend =  requestSunatTemplate.buildSendBill(fileName, contentFileBase64);
        }
        return buildsend ;
    }

    private String obtenerStatusCdr(GetStatusCdrDTO statusDto, String ruc) {
        OseInterDto ose = companyRepository.findOseByRucInter(ruc);
        String statusstring = "";
        if (ose != null) {
            if (ose.getId()==1){
                statusstring =   requestSunatTemplate.buildOseGetStatusCDR(statusDto);
            }else if (ose.getId()==2){
                statusstring =   requestSunatTemplate.buildOseBlizGetStatusCDR(statusDto);
            }
        } else {
            statusstring =   requestSunatTemplate.buildGetStatusCDR(statusDto);
        }
        return  statusstring ;

    }

    private void buildResponseGetStatusCDR(ResponseSunat responseSunat, ResponseServer responseServer) {

        Document document;
        NodeList nodeCode;
        NodeList nodeMessage;
        NodeList nodeContentResponse;
        Node node;
        String message;

        document = parseXmlFile(responseServer.getContent());

        if (responseServer.isSuccess()) {

            nodeContentResponse = document.getElementsByTagName("content");
            node = nodeContentResponse.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {
                responseSunat.setContentBase64(node.getTextContent());
                responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS);
            } else {
                responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITHOUT_CONTENT_CDR);
            }

            nodeCode = document.getElementsByTagName("statusCode");
            nodeMessage = document.getElementsByTagName("statusMessage");
            node = nodeCode.item(0);
            message = "[" + node.getTextContent() + "] ";
            node = nodeMessage.item(0);
            message = message + node.getTextContent();

            responseSunat.setSuccess(true);
            responseSunat.setMessage(message);

            System.out.println("Response sunat cdr 1: "+responseSunat.toString());

        } else {

            String valueFaultcode = null;
            String valueFaultstring = null;

            nodeCode = document.getElementsByTagName("faultcode");
            nodeMessage = document.getElementsByTagName("faultstring");
            node = nodeCode.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                valueFaultcode = (node.getTextContent()).replaceAll("[^0-9]", "");
                valueFaultstring = nodeMessage.item(0).getTextContent();
                if (valueFaultcode.equals("")) {
                    responseSunat.setMessage("Error al comunicarse con la Sunat." + valueFaultstring);
                    responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.WITHOUT_CONNECTION);
                } else {
                    responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                    responseSunat.setStatusCode(valueFaultcode);
                    responseSunat.setMessage(valueFaultstring);
                }
            }
            responseSunat.setSuccess(false);
            System.out.println("Response sunat cdr 2: "+responseSunat.toString());
        }
    }

    private void buildResponseSendOtherCpe(ResponseSunat responseSunat, ResponseServer responseServer, String nameTagContent) throws DOMException, IOException {

        Document document;
        NodeList nodeFaultcode;
        NodeList nodeFaultstring;
        NodeList nodeContentBase64;
        Node node;
        Node nodeStatusCode;
        Map<String, String> datosCDR;
        List<String> codigosResponse;
        List<String> mensajesResponse;
        String nameDocumentResponse;
        String tipoDocumento;
        String rucEmisor;
        boolean isWarning = true;
        StringBuilder messageResponse = null;

        document = parseXmlFile(responseServer.getContent());

        if (responseServer.isSuccess()) {

            nodeContentBase64 = document.getElementsByTagName("applicationResponse");
            node = nodeContentBase64.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                if (nameTagContent.equals(ConstantesParameter.TAG_GET_STATUS_CONTENT)) {
                    nodeStatusCode = document.getElementsByTagName(ConstantesParameter.TAG_STATUS_CODE).item(0);
                    if (!nodeStatusCode.getTextContent().equals(ConstantesParameter.CODE_RESPONSE_OK)) {
                        responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                        responseSunat.setStatusCode(RebuildFile.getDataResponseFromCDR(node.getTextContent()).get(ConstantesParameter.PARAM_RESPONSE_CODE));
                        responseSunat.setMessage(RebuildFile.getDataResponseFromCDR(node.getTextContent()).get(ConstantesParameter.PARAM_DESCRIPTION));
                        return;
                    }
                }

                datosCDR = RebuildFile.getDataResponseFromCDR(node.getTextContent());
                nameDocumentResponse = datosCDR.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
                tipoDocumento = datosCDR.get(ConstantesParameter.PARAM_TIPO_ARCHIVO);
                rucEmisor = datosCDR.get(ConstantesParameter.PARAM_RUC_EMISOR);

                if (((String) datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE)).contains("|")) {

                    codigosResponse = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE).split("|"));
                    mensajesResponse = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION).split("|"));

                    for (int i = 0; i < codigosResponse.size(); i++) {

                        validateCodeReponseFromCDR(
                                responseSunat,
                                codigosResponse.get(i),
                                tipoDocumento,
                                mensajesResponse.get(i));

                        if (responseSunat.getEstadoComunicacionSunat().equals(ComunicacionSunatEnum.SUCCESS)) {
                            responseSunat.setContentBase64(node.getTextContent());
                            isWarning = false;
                            break;
                        }
                        if (responseSunat.getEstadoComunicacionSunat().equals(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT)) {
                            isWarning = false;
                            break;
                        }
                        if (messageResponse == null) {
                            messageResponse = new StringBuilder();
                        }
                        messageResponse.append("[").append(codigosResponse.get(i)).append("] ").append(mensajesResponse.get(i));
                        if ((i + 1) < codigosResponse.size()) {
                            messageResponse.append("|");
                        }
                    }
                    if (isWarning) {
                        responseSunat.setMessage(messageResponse.toString());
                        System.out.println("mensaje de respuesta: "+responseSunat.getMessage());
                    }
                } else {
                    validateCodeReponseFromCDR(
                            responseSunat,
                            datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE),
                            tipoDocumento,
                            datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION)
                    );

                    if (responseSunat.getEstadoComunicacionSunat().equals(ComunicacionSunatEnum.SUCCESS)) {
                        responseSunat.setContentBase64(node.getTextContent());
                    }
                }

                responseSunat.setNameDocument(nameDocumentResponse);
                responseSunat.setRucEmisor(rucEmisor);
                responseSunat.setStatusCode(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE));
            } else {
                responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITHOUT_CONTENT_CDR);
                responseSunat.setMessage(ConstantesParameter.MENSAJE_NO_FOUND_CDR);
                responseSunat.setSuccess(true);
            }
        } else {

            String valueFaultcode = null;
            String valueFaultstring = null;

            nodeFaultcode = document.getElementsByTagName("faultcode");
            nodeFaultstring = document.getElementsByTagName("detail");
            node = nodeFaultcode.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                valueFaultcode = (node.getTextContent()).replaceAll("[^0-9]", "");
                valueFaultstring = nodeFaultstring.item(0).getTextContent();
                if (valueFaultcode.equals("")) {
                    responseSunat.setMessage("Error al comunicarse con la Sunat." + valueFaultstring);
                    responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.WITHOUT_CONNECTION);
                } else {
                    responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                    responseSunat.setStatusCode(valueFaultcode);
                    responseSunat.setMessage(valueFaultstring);
                }
            }else {
                nodeFaultcode = document.getElementsByTagName(ConstantesParameter.TAG_STATUS_CODE);
                node = nodeFaultcode.item(0);
                String code = node.getTextContent();
                if (node != null && StringUtils.isNotBlank(code)) {
                    log.info("STATUS CODE SUNAT: {}", code);
                    if (code.equals("98") || code.equals("0098")) {
                        responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.PENDING);
                        responseSunat.setStatusCode(code);
                        responseSunat.setMessage("Sunat: Ticket se encuentra en proceso");
                    }
                }
            }
            responseSunat.setSuccess(false);
        }
    }

    private void buildResponseSendBillStatus(ResponseSunat responseSunat, ResponseServer responseServer,
                                             String nameTagContent) throws DOMException, IOException {

        Document document;
        NodeList nodeFaultcode;
        NodeList nodeFaultstring;
        NodeList nodeContentBase64;
        Node node;
        Node nodeStatusCode;
        Map<String, String> datosCDR;
        List<String> codigosResponse;
        List<String> mensajesResponse;
        String nameDocumentResponse;
        String tipoDocumento;
        String rucEmisor;
        boolean isWarning = true;
        StringBuilder messageResponse = null;


        document = parseXmlFile(responseServer.getContent());

        if (responseServer.isSuccess()) {


            nodeContentBase64 = document.getElementsByTagName(nameTagContent);
            node = nodeContentBase64.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                if (nameTagContent.equals(ConstantesParameter.TAG_GET_STATUS_CONTENT)) {
                    nodeStatusCode = document.getElementsByTagName(ConstantesParameter.TAG_STATUS_CODE).item(0);

                    if (!nodeStatusCode.getTextContent().equals(ConstantesParameter.CODE_RESPONSE_OK)) {

                        responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                        responseSunat.setStatusCode(RebuildFile.getDataResponseFromCDR(node.getTextContent()).get(ConstantesParameter.PARAM_RESPONSE_CODE));
                        responseSunat.setMessage(RebuildFile.getDataResponseFromCDR(node.getTextContent()).get(ConstantesParameter.PARAM_DESCRIPTION));
                        return;
                    }
                }

                datosCDR = RebuildFile.getDataResponseFromCDR(node.getTextContent());
                nameDocumentResponse = datosCDR.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
                tipoDocumento = datosCDR.get(ConstantesParameter.PARAM_TIPO_ARCHIVO);
                rucEmisor = datosCDR.get(ConstantesParameter.PARAM_RUC_EMISOR);


                if (((String) datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE)).contains("|")) {


                    codigosResponse = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE).split("|"));
                    mensajesResponse = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION).split("|"));

                    for (int i = 0; i < codigosResponse.size(); i++) {

                        validateCodeReponseFromCDR(
                                responseSunat,
                                codigosResponse.get(i),
                                tipoDocumento,
                                mensajesResponse.get(i));

                        if (responseSunat.getEstadoComunicacionSunat().equals(ComunicacionSunatEnum.SUCCESS)) {

                            responseSunat.setContentBase64(node.getTextContent());
                            isWarning = false;
                            break;
                        }
                        if (responseSunat.getEstadoComunicacionSunat().equals(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT)) {

                            isWarning = false;
                            break;
                        }
                        if (messageResponse == null) {

                            messageResponse = new StringBuilder();
                        }
                        messageResponse.append("[").append(codigosResponse.get(i)).append("] ").append(mensajesResponse.get(i));
                        if ((i + 1) < codigosResponse.size()) {
                            messageResponse.append("|");
                        }
                    }
                    if (isWarning) {
                        responseSunat.setMessage(messageResponse.toString());
                    }
                } else {
                    validateCodeReponseFromCDR(
                            responseSunat,
                            datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE),
                            tipoDocumento,
                            datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION)
                    );

                    if (responseSunat.getEstadoComunicacionSunat().equals(ComunicacionSunatEnum.SUCCESS)) {

                        responseSunat.setContentBase64(node.getTextContent());
                    }
                }
                responseSunat.setNameDocument(nameDocumentResponse);
                responseSunat.setRucEmisor(rucEmisor);
                responseSunat.setStatusCode(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE));
            } else {
                responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITHOUT_CONTENT_CDR);
                responseSunat.setMessage(ConstantesParameter.MENSAJE_NO_FOUND_CDR);
                responseSunat.setSuccess(true);
            }

        } else {

            String valueFaultcode = null;
            String valueFaultstring = null;

            nodeFaultcode = document.getElementsByTagName("faultcode");
            nodeFaultstring = document.getElementsByTagName("faultstring");
            node = nodeFaultcode.item(0);

            if (node != null && StringUtils.isNotBlank(node.getTextContent())) {

                valueFaultcode = (node.getTextContent()).replaceAll("[^0-9]", "");
                valueFaultstring = nodeFaultstring.item(0).getTextContent();
                if (valueFaultcode.equals("")) {
                    responseSunat.setMessage("Error al comunicarse con la Sunat." + valueFaultstring);
                    responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.WITHOUT_CONNECTION);
                } else {
                    responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                    responseSunat.setStatusCode(valueFaultcode);
                    responseSunat.setMessage(valueFaultstring);
                }
            } else {
                nodeFaultcode = document.getElementsByTagName(ConstantesParameter.TAG_STATUS_CODE);
                node = nodeFaultcode.item(0);
                String code = node.getTextContent();
                if (node != null && StringUtils.isNotBlank(code)) {
                    log.info("STATUS CODE SUNAT: {}", code);
                    if (code.equals("98") || code.equals("0098")) {
                        responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.PENDING);
                        responseSunat.setStatusCode(code);
                        responseSunat.setMessage("Sunat: Ticket se encuentra en proceso");
                    }
                }
            }
            responseSunat.setSuccess(false);
        }
    }

    private void validateCodeReponseFromCDR(ResponseSunat responseSunat, String codigoRespuesta, String tipoDocumento, String mensajeRespuesta) {

        ErrorEntity errorRespuesta;

        if (codigoRespuesta.equals(ConstantesParameter.CODIGO_ACEPTADO_FROM_CDR)) {

            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS);
            responseSunat.setMessage(mensajeRespuesta);
            responseSunat.setStatusCode(codigoRespuesta);
            responseSunat.setSuccess(true);

            return;
        }

        errorRespuesta = errorRepository.findFirst1ByCodeAndDocument(codigoRespuesta, tipoDocumento);

        if (errorRespuesta != null) {
            if (errorRespuesta.getType().equals(TypeErrorEnum.ERROR.getType())) {

                responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
                responseSunat.setSuccess(false);
            } else {
                responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITH_WARNING);
                responseSunat.setSuccess(true);
            }
            responseSunat.setStatusCode(codigoRespuesta);
            responseSunat.setMessage(mensajeRespuesta);
        } else {

            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.SUCCESS_WITH_ERROR_CONTENT);
            responseSunat.setStatusCode(ConstantesParameter.CODIGO_NO_FOUND_CODE_ERROR_FROM_CDR);
            responseSunat.setMessage(ConstantesParameter.MENSAJE_NO_FOUND_CODE_FROM_CDR + ". [ResponseCode:" + codigoRespuesta + "][Tipo documento:" + tipoDocumento + "][Description:" + mensajeRespuesta + "]");
            responseSunat.setSuccess(false);
        }

    }

    private ResponseServer send(String xml, String endpoint, String tagOperacionOK)
            throws IOException {
        System.out.println(endpoint);
        System.out.println(xml);
        ResponseServer responseServer = new ResponseServer();
        CloseableHttpResponse responsePost;
        String formattedSOAPResponse;
        CloseableHttpClient client = null;
        StringEntity entity = null;
        HttpPost httpPost = null;
        String inputLine;
        int responseCode = 0;

        client = HttpClients.createDefault();
        httpPost = new HttpPost(endpoint);
        httpPost.setHeader("Prama", "no-cache");
        httpPost.setHeader("Cache-Control", "no-cache");

        entity = new StringEntity(xml, "UTF-8");
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-type", "text/xml");

        responsePost = client.execute(httpPost);
        responseCode = responsePost.getStatusLine().getStatusCode();
        responseServer.setServerCode(responseCode);

        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(responsePost.getEntity().getContent()));
        while ((inputLine = in.readLine()) != null) {
            String inp = inputLine.replace("S:","soap-env:");
            inp = inp.replace(":S=",":soap-env=");
            inp = inp.replace("SOAP-ENV:","soap-env:");
            inp = inp.replace("ns2:","br:");
            inp = inp.replace(":ns2",":br");
            response.append(inp);
            //response.append(inputLine);
        }
        System.out.println("response");
        System.out.println(response);
        client.close();
        formattedSOAPResponse = formatXML(response.toString());
        responseServer.setContent(formattedSOAPResponse);

        if (formattedSOAPResponse.contains("<" + tagOperacionOK + ">")) {
            responseServer.setSuccess(true);
        } else {
            responseServer.setSuccess(false);
        }

        return responseServer;
    }

    @Override
    public ResponseSunat sendBillGuiaRemision(String fileName, String contentFileBase64, String rucEmisor) {
        ResponseSunat responseSunat = new ResponseSunat();
        try {
            String formatSoap = obtenerBuildSendBill(rucEmisor,fileName, contentFileBase64);
            System.out.println(obtenerEndPointGuia(rucEmisor));
            ResponseServer responseServer = null;
            OseInterDto ose = companyRepository.findOseByRucInter(rucEmisor);
            if (ose != null) {
                if (ose.getId()==1){
                    responseServer = send(formatSoap, obtenerEndPointSunat(rucEmisor),
                            ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
                }else if (ose.getId()==2){
                    RestTemplate template = new RestTemplate();
                    URI uriget = new URI(ose.getUrlguias()+ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);
                    HttpHeaders requestHeaders = new HttpHeaders();
                    HttpEntity<String> requestEntity = new HttpEntity<>(formatSoap, requestHeaders);
                    ResponseEntity<ResponseServer> entity = template.exchange(uriget, HttpMethod.POST, requestEntity, ResponseServer.class);
                    System.out.println("PUENTE GUIA OSE BLIZ");
                    System.out.println(entity);
                    if(entity.getStatusCode() == HttpStatus.OK){
                        responseServer = entity.getBody();
                        System.out.println("RESULTADO CONSULTA GUIA ");
                    }
                }
            } else {
                responseServer = send(
                        formatSoap,
                        obtenerEndPointGuia(rucEmisor),
                        ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE
                );
            }
            buildResponseSendOtherCpe(responseSunat, responseServer, ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);

        } catch (IOException e) {
            responseSunat.setMessage("Error al comunicarse con la Sunat." + e.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.WITHOUT_CONNECTION);
        } catch (Exception ex) {
            responseSunat.setMessage(ex.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.ERROR_INTERNO_WS_API);
        }
        return responseSunat;
    }

    @Override
    public ResponseSunat sendBillOtrosCpe(String fileName, String contentFileBase64, String rucEmisor) {

        ResponseSunat responseSunat = new ResponseSunat();
        try {

            String formatSoap = obtenerBuildSendBill(rucEmisor,fileName, contentFileBase64);
            ResponseServer responseServer = send(
                    formatSoap,
                    obtenerEndPointSunatOtrosCpe(rucEmisor),
                    ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE
            );
            buildResponseSendOtherCpe(responseSunat, responseServer, ConstantesParameter.TAG_SEND_BILL_APPLICATION_RESPONSE);

        } catch (IOException e) {

            responseSunat.setMessage("Error al comunicarse con la Sunat." + e.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.WITHOUT_CONNECTION);
            e.printStackTrace();
        } catch (Exception ex) {
            responseSunat.setMessage(ex.getMessage());
            responseSunat.setSuccess(false);
            responseSunat.setEstadoComunicacionSunat(ComunicacionSunatEnum.ERROR_INTERNO_WS_API);
            ex.printStackTrace();
        }

        return responseSunat;
    }
}
