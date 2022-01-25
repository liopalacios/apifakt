package pe.com.certifakt.apifact.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.bean.ResponsePSE;
import pe.com.certifakt.apifact.bean.ResponseSunat;
import pe.com.certifakt.apifact.dto.GetStatusCdrDTO;
import pe.com.certifakt.apifact.dto.GetStatusExcelDTO;
import pe.com.certifakt.apifact.dto.SendBillDTO;
import pe.com.certifakt.apifact.dto.SendOtherDocumentDTO;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.enums.*;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.*;
import pe.com.certifakt.apifact.service.AmazonS3ClientService;
import pe.com.certifakt.apifact.service.ComunicationSunatService;
import pe.com.certifakt.apifact.service.SendSunatService;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.Logger;
import pe.com.certifakt.apifact.util.RebuildFile;
import pe.com.certifakt.apifact.util.UtilArchivo;

import java.io.*;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class ComunicationSunatServiceImpl implements ComunicationSunatService {

    @Autowired
    private ExcelServiceImpl excelService;

    @Autowired
    private DowloadExcelRepository dowloadExcelRepository;

    private final PaymentVoucherRepository paymentVoucherRepository;
    private final TmpVoucherSendBillRepository tmpVoucherSendBillRepository;
    private final OtherCpeRepository otherCpeRepository;
    private final GuiaRemisionRepository guiaRemisionRepository;
    private final AmazonS3ClientService amazonS3ClientService;
    private final SendSunatService sendSunatService;
    private final CompanyRepository companyRepository;
    private final PaymentVoucherFileRepository paymentVoucherFileRepository;
    private final OtherCpeFileRepository otherCpeFileRepository;
    private final GuiaRemisionFileRepository guiaRemisionFileRepository;
    private final ExcelRepository excelRepository;

    @Transactional
    @Override
    public Map<String, Object> sendDocumentBill( String ruc, Long idPaymentVoucher) {

        TmpVoucherSendBillEntity voucherPendiente = null;
        Map<String, Object> result = new HashMap<>();
        ResponsePSE resp = new ResponsePSE();
        ResponseSunat responseSunat;
        ResponseSunat responseSunatCdr;
        String fileXMLZipBase64;
        String messageResponse = null;
        String nombreCompleto;
        Boolean status = null;
        StringBuilder msgLog = new StringBuilder();

        try {
            voucherPendiente = tmpVoucherSendBillRepository.findByIdPaymentVoucher(idPaymentVoucher);

            if (voucherPendiente != null) {

                Logger.register(TipoLogEnum.INFO, ruc, voucherPendiente.getNombreDocumento(),
                        OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.SELECT_BD,
                        ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + " [" + voucherPendiente.toString() + "]");

                tmpVoucherSendBillRepository.updateStatusVoucherTmp(
                        voucherPendiente.getIdTmpSendBill(),
                        EstadoVoucherTmpEnum.BLOQUEO.getEstado()
                );
                msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}{voucherTmp:").
                        append(voucherPendiente.toString()).append("}");
                Logger.register(TipoLogEnum.INFO, ruc, voucherPendiente.getNombreDocumento(),
                        OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.UPDATE_BD,
                        msgLog.toString());


                //PaymentVoucherFileEntity paymentVoucherFileXml = paymentVoucherFileRepository.findFirst1ByPaymentVoucher_IdPaymentVoucherAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(idPaymentVoucher, TipoArchivoEnum.XML, EstadoArchivoEnum.ACTIVO);
                RegisterFileUploadInterDto registerFileUploadInterDto = paymentVoucherFileRepository
                        .findFirst1ByPaymentVoucherIdPaymentVoucherAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(idPaymentVoucher, TipoArchivoEnum.XML.name(), EstadoArchivoEnum.ACTIVO.name());
                System.out.println("BASE 64 01");

                if (registerFileUploadInterDto == null)
                    throw new ServiceException("No se encuentra el archivo XML a enviar, por favor edite o regenere el comprobante.");
                fileXMLZipBase64 = amazonS3ClientService.downloadFileStorageInB64(registerFileUploadInterDto);

                msgLog.delete(0, msgLog.length() - 1);
                msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                        append("{uuid:").append(voucherPendiente.getUuidSaved()).append("}{ruc:").
                        append(ruc).append("}");
                Logger.register(TipoLogEnum.INFO, ruc, voucherPendiente.getNombreDocumento(),
                        OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.GET_FILE,
                        msgLog.toString());

                nombreCompleto = voucherPendiente.getNombreDocumento() + "." + ConstantesParameter.TYPE_FILE_ZIP;
                responseSunat = sendSunatService.sendBillPaymentVoucher(
                        nombreCompleto,
                        fileXMLZipBase64,ruc
                );
                Logger.register(TipoLogEnum.INFO, ruc, voucherPendiente.getNombreDocumento(),
                        OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.SEND_SUNAT,
                        responseSunat.toString());

                    switch (responseSunat.getEstadoComunicacionSunat()) {
                        case SUCCESS:
                            messageResponse = responseSunat.getMessage();
                            comunicacionSuccess(
                                    ruc,
                                    voucherPendiente.getTipoComprobante(),
                                    voucherPendiente.getIdTmpSendBill(),
                                    voucherPendiente.getIdPaymentVoucher(),
                                    responseSunat.getContentBase64(),
                                    responseSunat.getMessage(),
                                    EstadoComprobanteEnum.ACEPTADO.getCodigo(),
                                    responseSunat.getNameDocument(),
                                    responseSunat.getStatusCode()
                            );
                            status = true;
                            break;
                        case SUCCESS_WITH_WARNING:
                            messageResponse = responseSunat.getMessage();
                            comunicacionSuccess(
                                    ruc,
                                    voucherPendiente.getTipoComprobante(),
                                    voucherPendiente.getIdTmpSendBill(),
                                    voucherPendiente.getIdPaymentVoucher(),
                                    responseSunat.getContentBase64(),
                                    responseSunat.getMessage(),
                                    EstadoComprobanteEnum.ACEPTADO_ADVERTENCIA.getCodigo(),
                                    responseSunat.getNameDocument(),
                                    responseSunat.getStatusCode()
                            );
                            status = true;
                            break;

                        case SUCCESS_WITHOUT_CONTENT_CDR:

                            GetStatusCdrDTO dataGetStatusCDR;
                            messageResponse = responseSunat.getMessage();
                            dataGetStatusCDR = comunicacionWithoutContentCDRSendBill(
                                    voucherPendiente.getIdTmpSendBill(),
                                    voucherPendiente.getIdPaymentVoucher(),
                                    messageResponse,
                                    voucherPendiente.getNombreDocumento(),
                                    responseSunat.getStatusCode());
                            result.put(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR, dataGetStatusCDR);
                            status = false;
                            break;
                        case SUCCESS_WITH_ERROR_CONTENT:
                            if(Integer.parseInt(responseSunat.getStatusCode())==1033){
                                PaymentVoucherEntity paymentVoucherEntity = paymentVoucherRepository.findByIdPaymentVoucher(idPaymentVoucher);
                                GetStatusCdrDTO statusCdrDTO = new GetStatusCdrDTO(ruc,paymentVoucherEntity.getTipoComprobante(),paymentVoucherEntity.getSerie(),paymentVoucherEntity.getNumero(),idPaymentVoucher);
                                responseSunatCdr = sendSunatService.getStatusCDR(statusCdrDTO, ruc);

                                messageResponse = "La Factura numero "+paymentVoucherEntity.getSerie()+"-"+paymentVoucherEntity.getNumero()+", ha sido aceptada";

                                RegisterFileUploadEntity responseStorage = uploadFileCdr(ruc, voucherPendiente.getNombreDocumento(), voucherPendiente.getTipoComprobante(), ConstantesParameter.REGISTRO_STATUS_NUEVO,
                                        responseSunatCdr.getContentBase64());

                                paymentVoucherEntity.setEstado(EstadoComprobanteEnum.ACEPTADO.getCodigo());
                                paymentVoucherEntity.setEstadoSunat(EstadoSunatEnum.ACEPTADO.getAbreviado());
                                paymentVoucherEntity.setMensajeRespuesta(messageResponse);
                                paymentVoucherEntity.setCodigosRespuestaSunat("0");

                                if (responseStorage.getIdRegisterFileSend() != null) {
                                    paymentVoucherEntity.addFile(PaymentVoucherFileEntity.builder()
                                            .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                                            .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(responseStorage.getIdRegisterFileSend()).build())
                                            .tipoArchivo(TipoArchivoEnum.CDR)
                                            .build());
                                }
                                paymentVoucherRepository.save(paymentVoucherEntity);

                                status = true;
                            }else{
                            comunicacionWithErrorSendBill(
                                    idPaymentVoucher,
                                    voucherPendiente.getIdTmpSendBill(),
                                    responseSunat.getMessage(),
                                    responseSunat.getStatusCode()
                            );
                            status = false;
                            messageResponse = responseSunat.getMessage();
                            }
                            break;
                        case WITHOUT_CONNECTION:
                            if(responseSunat.getMessage().contains("1033")){
                                PaymentVoucherEntity paymentVoucherEntity = paymentVoucherRepository.findByIdPaymentVoucher(idPaymentVoucher);

                                GetStatusCdrDTO statusCdrDTO = new GetStatusCdrDTO(ruc,paymentVoucherEntity.getTipoComprobante(),paymentVoucherEntity.getSerie(),paymentVoucherEntity.getNumero(),idPaymentVoucher);

                                responseSunatCdr = sendSunatService.getStatusCDR(statusCdrDTO, ruc);

                                messageResponse = "La Factura numero "+paymentVoucherEntity.getSerie()+"-"+paymentVoucherEntity.getNumero()+", ha sido aceptada";


                                RegisterFileUploadEntity responseStorage = uploadFileCdr(ruc, voucherPendiente.getNombreDocumento(), voucherPendiente.getTipoComprobante(), ConstantesParameter.REGISTRO_STATUS_NUEVO,
                                        responseSunatCdr.getContentBase64());

                                paymentVoucherEntity.setEstado(EstadoComprobanteEnum.ACEPTADO.getCodigo());
                                paymentVoucherEntity.setEstadoSunat(EstadoSunatEnum.ACEPTADO.getAbreviado());
                                paymentVoucherEntity.setMensajeRespuesta(messageResponse);
                                paymentVoucherEntity.setCodigosRespuestaSunat("0");

                                if (responseStorage.getIdRegisterFileSend() != null) {
                                    paymentVoucherEntity.addFile(PaymentVoucherFileEntity.builder()
                                            .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                                            .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(responseStorage.getIdRegisterFileSend()).build())
                                            .tipoArchivo(TipoArchivoEnum.CDR)
                                            .build());
                                }
                                paymentVoucherRepository.save(paymentVoucherEntity);

                                status = true;
                            }else{
                                System.out.println("Error: Dio error al reenviar");
                                comunicacionWithoutConnectionSendBill(
                                        voucherPendiente.getIdTmpSendBill()
                                );
                                result.put(ConstantesParameter.PARAM_BEAN_SEND_BILL, SendBillDTO.builder().ruc(ruc).idPaymentVoucher(idPaymentVoucher).nameDocument(voucherPendiente.getNombreDocumento()).envioAutomaticoSunat(true).build());
                                status = false;
                                messageResponse = responseSunat.getMessage();
                            }
                            break;
                        default:

                    }

            } else {
                throw new Exception(
                        "No se pudo entontrar en la tabla temporal id_payment_voucher[" + idPaymentVoucher + "]");
            }
        } catch (Exception e) {

            status = false;
            messageResponse = e.getMessage();

            Logger.register(TipoLogEnum.ERROR, ruc, voucherPendiente.getNombreDocumento(),
                    OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.IN_PROCESS,
                    messageResponse, idPaymentVoucher.toString(), e);

        }

        resp.setMensaje(messageResponse);
        resp.setEstado(status);
        resp.setNombre(voucherPendiente.getNombreDocumento());

        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, resp);

        Logger.register(TipoLogEnum.INFO, ruc, voucherPendiente.getNombreDocumento(),
                OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.COMPLETED, result.toString());

        return result;
    }

    @Transactional
    @Override
    public Map<String, Object> getStatusBill(
            GetStatusCdrDTO getStatusCdr) {

        TmpVoucherSendBillEntity voucherPendiente = null;
        Map<String, Object> result = new HashMap<>();
        ResponsePSE resp = new ResponsePSE();
        ResponseSunat responseSunat;
        String messageResponse = null;
        Boolean status = null;

        try {
            voucherPendiente = tmpVoucherSendBillRepository.
                    findByIdPaymentVoucher(getStatusCdr.getIdPaymentVoucher());


            if (voucherPendiente != null) {

                Logger.register(TipoLogEnum.INFO, getStatusCdr.getRuc(), voucherPendiente.getNombreDocumento(),
                        OperacionLogEnum.GET_STATUS_PAYMENT_VOUCHER, SubOperacionLogEnum.SELECT_BD,
                        ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + " [" + voucherPendiente.toString() + "]");

                tmpVoucherSendBillRepository.updateStatusVoucherTmp(
                        voucherPendiente.getIdTmpSendBill(),
                        EstadoVoucherTmpEnum.BLOQUEO.getEstado()
                );
                Logger.register(TipoLogEnum.INFO, getStatusCdr.getRuc(), voucherPendiente.getNombreDocumento(),
                        OperacionLogEnum.GET_STATUS_PAYMENT_VOUCHER, SubOperacionLogEnum.UPDATE_BD,
                        ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

                responseSunat = sendSunatService.getStatusCDR(getStatusCdr,getStatusCdr.getRuc());
                Logger.register(TipoLogEnum.INFO, getStatusCdr.getRuc(), voucherPendiente.getNombreDocumento(),
                        OperacionLogEnum.GET_STATUS_PAYMENT_VOUCHER, SubOperacionLogEnum.SEND_SUNAT,
                        responseSunat.toString());

                switch (responseSunat.getEstadoComunicacionSunat()) {
                    case SUCCESS:

                        messageResponse = comunicacionSuccessStatus(
                                getStatusCdr.getRuc(),
                                voucherPendiente.getTipoComprobante(),
                                voucherPendiente.getIdTmpSendBill(),
                                voucherPendiente.getIdPaymentVoucher(),
                                responseSunat.getContentBase64()
                        );
                        status = true;
                        break;
                    case SUCCESS_WITHOUT_CONTENT_CDR:

                        messageResponse = "El contenido CDR se encuentra vacio." + responseSunat.getMessage();
                        status = false;
                        break;
                    case SUCCESS_WITH_ERROR_CONTENT:

                        status = false;
                        messageResponse = responseSunat.getMessage();
                        break;
                    case WITHOUT_CONNECTION:

                        comunicacionWithoutConnectionGetStatusCDR(
                                voucherPendiente.getIdTmpSendBill()
                        );
                        result.put(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR, getStatusCdr);
                        status = false;
                        messageResponse = responseSunat.getMessage();
                        break;
                    default:

                }
            } else {
                throw new Exception("No se pudo entontrar en la tabla temporal id_payment_voucher[" +
                        getStatusCdr.getIdPaymentVoucher() + "]");
            }
        } catch (Exception e) {
            status = false;
            messageResponse = e.getMessage();

            Logger.register(TipoLogEnum.ERROR, getStatusCdr.getRuc(), voucherPendiente.getNombreDocumento(),
                    OperacionLogEnum.GET_STATUS_PAYMENT_VOUCHER, SubOperacionLogEnum.IN_PROCESS,
                    messageResponse, getStatusCdr.toString(), e);

        }

        resp.setMensaje(messageResponse);
        resp.setEstado(status);
        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, resp);

        Logger.register(TipoLogEnum.INFO, getStatusCdr.getRuc(), voucherPendiente.getNombreDocumento(),
                OperacionLogEnum.GET_STATUS_PAYMENT_VOUCHER, SubOperacionLogEnum.COMPLETED, result.toString());

        return result;
    }



    @Transactional
    @Override
    public Map<String, Object> sendOtrosCpe(SendOtherDocumentDTO otroCpe) {

        Map<String, Object> result = new HashMap<>();
        ResponsePSE resp = new ResponsePSE();
        ResponseSunat responseSunat;
        String fileXMLZipBase64;
        String messageResponse = null;
        String nombreCompleto;
        Boolean status = null;

        try {
            otherCpeRepository.updateEstadoHabilitarOrDeshabiltarRegistro(
                    otroCpe.getIdVoucher(),
                    EstadoComprobanteEnum.PROCESO_ENVIO.getCodigo());


            //OtherCpeFileEntity otherCpeFileXml = otherCpeFileRepository.findFirst1ByOtherCpe_IdOtroCPEAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(otroCpe.getIdVoucher(), TipoArchivoEnum.XML, EstadoArchivoEnum.ACTIVO);
            RegisterFileUploadInterDto registerFileUploadInterDto = otherCpeFileRepository.findFirst1ByOtherCpeIdOtroCPEAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(otroCpe.getIdVoucher(), TipoArchivoEnum.XML.name(), EstadoArchivoEnum.ACTIVO.name());
            System.out.println("BASE 64 02");
            fileXMLZipBase64 = amazonS3ClientService.downloadFileStorageInB64(registerFileUploadInterDto);

            nombreCompleto = otroCpe.getNameDocument() + "." + ConstantesParameter.TYPE_FILE_ZIP;

            responseSunat = sendSunatService.sendBillOtrosCpe(
                    nombreCompleto,
                    fileXMLZipBase64,
                    otroCpe.getRuc()
            );
            switch (responseSunat.getEstadoComunicacionSunat()) {
                case SUCCESS:

                    messageResponse = comunicacionSuccessOtrosCpe(otroCpe, responseSunat.getContentBase64());
                    status = true;
                    break;
                case SUCCESS_WITHOUT_CONTENT_CDR:
                    comunicacionConErrorOtrosCpe(
                            EstadoComprobanteEnum.ACEPTADO_POR_VERIFICAR.getCodigo(),
                            EstadoSunatEnum.RECHAZADO.getAbreviado(),
                            otroCpe.getIdVoucher(),
                            responseSunat.getMessage());
                    status = false;
                    break;
                case SUCCESS_WITH_ERROR_CONTENT:

                    comunicacionConErrorOtrosCpe(
                            EstadoComprobanteEnum.ERROR.getCodigo(),
                            EstadoSunatEnum.RECHAZADO.getAbreviado(),
                            otroCpe.getIdVoucher(),
                            responseSunat.getMessage());
                    status = false;
                    messageResponse = responseSunat.getMessage();
                    break;
                case WITHOUT_CONNECTION:

                    otherCpeRepository.updateEstadoHabilitarOrDeshabiltarRegistro(
                            otroCpe.getIdVoucher(),
                            EstadoComprobanteEnum.REGISTRADO.getCodigo());
                    result.put(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE, otroCpe);
                    status = false;
                    messageResponse = responseSunat.getMessage();
                    break;
                default:

            }

        } catch (Exception e) {
            status = false;
            messageResponse = e.getMessage();
            e.printStackTrace();
        }

        resp.setMensaje(messageResponse);
        resp.setEstado(status);
        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, resp);

        return result;
    }

    @Transactional
    @Override
    public Map<String, Object> sendGuiaRemision(SendOtherDocumentDTO dataGuiaRemision) {

        Map<String, Object> result = new HashMap<>();
        ResponsePSE resp = new ResponsePSE();
        ResponseSunat responseSunat;
        String fileXMLZipBase64;
        String messageResponse = null;
        String nombreCompleto;
        Boolean status = null;

        try {
            GuiaRemisionEntity guia = guiaRemisionRepository.findByIdGuiaRemision(dataGuiaRemision.getIdVoucher());
            if (!guia.getEstado().equals("01")){
                return null;
            }
            guiaRemisionRepository.updateEstadoHabilitarOrDeshabiltarRegistro(
                    dataGuiaRemision.getIdVoucher(),
                    EstadoComprobanteEnum.PROCESO_ENVIO.getCodigo());


            //GuiaRemisionFileEntity guiaRemisionFileXml = guiaRemisionFileRepository.findFirst1ByGuiaRemision_IdGuiaRemisionAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(dataGuiaRemision.getIdVoucher(), TipoArchivoEnum.XML, EstadoArchivoEnum.ACTIVO);
            RegisterFileUploadInterDto registerFileUploadInterDto = guiaRemisionFileRepository.findFirst1ByGuiaRemisionIdGuiaRemisionAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(dataGuiaRemision.getIdVoucher(), TipoArchivoEnum.XML.name(), EstadoArchivoEnum.ACTIVO.name());
            System.out.println("BASE 64 03");
            fileXMLZipBase64 = amazonS3ClientService.downloadFileStorageInB64(registerFileUploadInterDto);
            nombreCompleto = dataGuiaRemision.getNameDocument() + "." + ConstantesParameter.TYPE_FILE_ZIP;

            responseSunat = sendSunatService.sendBillGuiaRemision(
                    nombreCompleto,
                    fileXMLZipBase64, dataGuiaRemision.getRuc()
            );

            System.out.println("Response Sunat");
            System.out.println(responseSunat);
            switch (responseSunat.getEstadoComunicacionSunat()) {
                case SUCCESS:
                    messageResponse = comunicacionSuccessGuiaRemision(
                            dataGuiaRemision,
                            responseSunat.getContentBase64(),
                            responseSunat.getMessage(),
                            EstadoComprobanteEnum.ACEPTADO.getCodigo(),
                            responseSunat.getNameDocument(),
                            responseSunat.getStatusCode());
                    status = true;
                    break;
                case SUCCESS_WITH_WARNING:
                    messageResponse = responseSunat.getMessage();
                    comunicacionSuccessGuiaRemision(
                            dataGuiaRemision,
                            responseSunat.getContentBase64(),
                            responseSunat.getMessage(),
                            EstadoComprobanteEnum.ACEPTADO_ADVERTENCIA.getCodigo(),
                            responseSunat.getNameDocument(),
                            responseSunat.getStatusCode());
                    status = true;
                    break;
                case SUCCESS_WITHOUT_CONTENT_CDR:
                    comunicacionConErrorGuiaRemision(
                            EstadoComprobanteEnum.ACEPTADO_POR_VERIFICAR.getCodigo(),
                            dataGuiaRemision.getIdVoucher(),
                            responseSunat.getMessage());
                    status = false;
                    messageResponse = responseSunat.getMessage();
                    break;

                case SUCCESS_WITH_ERROR_CONTENT:

                    comunicacionConErrorGuiaRemision(
                            EstadoComprobanteEnum.RECHAZADO.getCodigo(),
                            dataGuiaRemision.getIdVoucher(),
                            responseSunat.getMessage());
                    status = false;
                    messageResponse = responseSunat.getMessage();
                    break;

                case WITHOUT_CONNECTION:

                    guiaRemisionRepository.updateEstadoHabilitarOrDeshabiltarRegistro(
                            dataGuiaRemision.getIdVoucher(),
                            EstadoComprobanteEnum.RECHAZADO.getCodigo());
                    result.put(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE, dataGuiaRemision);
                    status = false;
                    messageResponse = responseSunat.getMessage();
                    break;
                default:

            }

        } catch (Exception e) {
            status = false;
            messageResponse = e.getMessage();
            e.printStackTrace();
        }

        resp.setMensaje(messageResponse);
        resp.setEstado(status);
        result.put(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE, resp);

        return result;
    }

    private String comunicacionSuccessStatus(String ruc, String tipoComprobante, Long idTmpVoucher, Long idPaymentVoucher,
                                             String contenidoBase64) throws Exception {

        StringBuilder messageResponse;
        Map<String, String> datosCDR;
        String nameDocumentResponse;
        List<String> descripciones;
        List<String> codigos;
        String estadoComprobante;

        messageResponse = new StringBuilder();
        datosCDR = RebuildFile.getDataResponseFromCDR(contenidoBase64);

        if (((String) datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE)).contains("|")) {

            codigos = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE).split("|"));
            descripciones = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION).split("|"));
            for (int i = 0; i < codigos.size(); i++) {
                messageResponse.append("[").append(codigos.get(i)).append("] ").append(descripciones.get(i));
                if ((i + 1) < codigos.size()) {
                    messageResponse.append("|");
                }
            }
            estadoComprobante = EstadoComprobanteEnum.ACEPTADO_ADVERTENCIA.getCodigo();
        } else {

            messageResponse.
                    append("[").
                    append(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE)).
                    append("] ").
                    append(datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION));
            estadoComprobante = EstadoComprobanteEnum.ACEPTADO.getCodigo();
        }

        nameDocumentResponse = datosCDR.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
        RegisterFileUploadEntity responseStorage = uploadFileCdr(ruc, nameDocumentResponse, tipoComprobante, ConstantesParameter.REGISTRO_STATUS_NUEVO,
                contenidoBase64);

        tmpVoucherSendBillRepository.deleteById(idTmpVoucher);

        PaymentVoucherEntity paymentVoucher = paymentVoucherRepository.findByIdPaymentVoucher(idPaymentVoucher);
        paymentVoucher.setEstado(estadoComprobante);
        paymentVoucher.setEstadoSunat(EstadoSunatEnum.ACEPTADO.getAbreviado());
        paymentVoucher.setMensajeRespuesta(messageResponse.toString());
        paymentVoucher.setCodigosRespuestaSunat(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE));

        //AGREGANDO ARCHIVO
        if (responseStorage.getIdRegisterFileSend() != null) {
            paymentVoucher.addFile(PaymentVoucherFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(responseStorage.getIdRegisterFileSend()).build())
                    .tipoArchivo(TipoArchivoEnum.CDR)
                    .build());
        }
        paymentVoucherRepository.save(paymentVoucher);

        return messageResponse.toString();
    }

    private GetStatusCdrDTO comunicacionWithoutContentCDRSendBill(
            Long idTmpSendBill,
            Long idPaymentVoucher,
            String messageResponse,
            String nameDocument,
            String codesResponse) {

        GetStatusCdrDTO dataGetStatus;
        String ruc;
        String tipoComprobante;
        String serie;
        Integer numero;

        tmpVoucherSendBillRepository.updateStatusVoucherTmp(
                idTmpSendBill,
                EstadoVoucherTmpEnum.VERIFICAR.getEstado());
        paymentVoucherRepository.updateEstadoComprobante(
                idPaymentVoucher,
                EstadoComprobanteEnum.ACEPTADO_POR_VERIFICAR.getCodigo(),
                messageResponse,
                codesResponse);

        String[] datosComprobante = nameDocument.split("-");
        ruc = datosComprobante[0];
        tipoComprobante = datosComprobante[1];
        serie = datosComprobante[2];
        numero = Integer.valueOf(datosComprobante[3]);

        dataGetStatus = new GetStatusCdrDTO();
        dataGetStatus.setRuc(ruc);
        dataGetStatus.setTipoComprobante(tipoComprobante);
        dataGetStatus.setSerie(serie);
        dataGetStatus.setNumero(numero);
        dataGetStatus.setIdPaymentVoucher(idPaymentVoucher);

        return dataGetStatus;
    }

    private void comunicacionWithErrorSendBill(
            Long idPaymentVoucher,
            Long idTmpSendBill,
            String messageResponse,
            String codesResponse) {

        tmpVoucherSendBillRepository.updateStatusVoucherTmp(
                idTmpSendBill,
                EstadoVoucherTmpEnum.ERROR.getEstado());
        paymentVoucherRepository.updateEstadoComprobante(
                idPaymentVoucher,
                EstadoComprobanteEnum.ERROR.getCodigo(),
                messageResponse,
                codesResponse);
    }

    private void comunicacionWithoutConnectionSendBill(Long idTmpSendBill) {

        tmpVoucherSendBillRepository.updateStatusVoucherTmp(
                idTmpSendBill,
                EstadoVoucherTmpEnum.PENDIENTE.getEstado()
        );
    }

    private void comunicacionWithoutConnectionGetStatusCDR(Long idTmpSendBill) {

        tmpVoucherSendBillRepository.updateStatusVoucherTmp(
                idTmpSendBill,
                EstadoVoucherTmpEnum.VERIFICAR.getEstado()
        );
    }

    private void comunicacionConErrorOtrosCpe(String estadoComprobante, String estadoSunat,
                                              Long idOtroCpe, String mensaje) {

        otherCpeRepository.updateEstadoComprobanteByResultadoSunat(
                idOtroCpe,
                estadoComprobante,
                estadoSunat,
                mensaje
        );
    }

    private void comunicacionConErrorGuiaRemision(String estadoComprobante,
                                                  Long idGuiaRemision, String mensaje) {

        guiaRemisionRepository.updateEstadoComprobanteByResultadoSunat(
                idGuiaRemision,
                estadoComprobante,
                mensaje
        );
    }

    private String comunicacionSuccessOtrosCpe(SendOtherDocumentDTO otroCpe,
                                               String contenidoBase64) throws Exception {

        StringBuilder messageResponse;
        Map<String, String> datosCDR;
        String nameDocumentResponse;
        List<String> descripciones;
        List<String> codigos;
        String estadoComprobante;

        messageResponse = new StringBuilder();
        datosCDR = RebuildFile.getDataResponseFromCDR(contenidoBase64);

        if (((String) datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE)).contains("|")) {

            codigos = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE).split("|"));
            descripciones = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION).split("|"));
            for (int i = 0; i < codigos.size(); i++) {
                messageResponse.append("[").append(codigos.get(i)).append("] ").append(descripciones.get(i));
                if ((i + 1) < codigos.size()) {
                    messageResponse.append("|");
                }
            }
            estadoComprobante = EstadoComprobanteEnum.ACEPTADO_ADVERTENCIA.getCodigo();
        } else {

            messageResponse.
                    append("[").
                    append(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE)).
                    append("] ").
                    append(datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION));
            estadoComprobante = EstadoComprobanteEnum.ACEPTADO.getCodigo();
        }

        nameDocumentResponse = datosCDR.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
        RegisterFileUploadEntity responseStorage = uploadFileCdr(otroCpe.getRuc(), nameDocumentResponse, otroCpe.getTipoComprobante(), ConstantesParameter.REGISTRO_STATUS_NUEVO,
                contenidoBase64);

        OtherCpeEntity otherCpeEntity = otherCpeRepository.findByIdOtroCPE(otroCpe.getIdVoucher());
        otherCpeEntity.setEstado(estadoComprobante);
        otherCpeEntity.setEstadoEnSunat(EstadoSunatEnum.ACEPTADO.getAbreviado());
        otherCpeEntity.setMensajeRespuesta(messageResponse.toString());

        //AGREGANDO ARCHIVO
        if (responseStorage.getIdRegisterFileSend() != null) {
            otherCpeEntity.addFile(OtherCpeFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(responseStorage.getIdRegisterFileSend()).build())
                    .tipoArchivo(TipoArchivoEnum.CDR)
                    .build());
        }

        otherCpeRepository.save(otherCpeEntity);

        return messageResponse.toString();
    }

    private String comunicacionSuccessGuiaRemision(SendOtherDocumentDTO dataGuiaRemision,
                                                   String contenidoBase64, String mensajeRespuesta, String estadoComprobante,
                                                   String nombreDocumento, String codigosRespuesta) throws Exception {

        StringBuilder messageResponse;
        Map<String, String> datosCDR;
        String nameDocumentResponse;
        List<String> descripciones;
        List<String> codigos;

        messageResponse = new StringBuilder();
        datosCDR = RebuildFile.getDataResponseFromCDR(contenidoBase64);

        if (((String) datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE)).contains("|")) {

            codigos = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE).split("|"));
            descripciones = Arrays.asList(datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION).split("|"));
            for (int i = 0; i < codigos.size(); i++) {
                messageResponse.append("[").append(codigos.get(i)).append("] ").append(descripciones.get(i));
                if ((i + 1) < codigos.size()) {
                    messageResponse.append("|");
                }
            }
            estadoComprobante = EstadoComprobanteEnum.ACEPTADO_ADVERTENCIA.getCodigo();
        } else {

            messageResponse.
                    append("[").
                    append(datosCDR.get(ConstantesParameter.PARAM_RESPONSE_CODE)).
                    append("] ").
                    append(datosCDR.get(ConstantesParameter.PARAM_DESCRIPTION));
            estadoComprobante = EstadoComprobanteEnum.ACEPTADO.getCodigo();
        }

        nameDocumentResponse = datosCDR.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
        RegisterFileUploadEntity responseStorage = uploadFileCdr(dataGuiaRemision.getRuc(), nameDocumentResponse, dataGuiaRemision.getTipoComprobante(), ConstantesParameter.REGISTRO_STATUS_NUEVO,
                contenidoBase64);

        GuiaRemisionEntity guiaRemisionEntity = guiaRemisionRepository.findByIdGuiaRemision(dataGuiaRemision.getIdVoucher());
        guiaRemisionEntity.setEstado(estadoComprobante);
        guiaRemisionEntity.setEstadoEnSunat(EstadoSunatEnum.ACEPTADO.getAbreviado());
        guiaRemisionEntity.setMensajeRespuesta(messageResponse.toString());

        //AGREGANDO ARCHIVO
        if (responseStorage.getIdRegisterFileSend() != null) {
            guiaRemisionEntity.addFile(GuiaRemisionFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(responseStorage.getIdRegisterFileSend()).build())
                    .tipoArchivo(TipoArchivoEnum.CDR)
                    .build());
        }

        guiaRemisionRepository.save(guiaRemisionEntity);

        if(dataGuiaRemision.getSerieBaja() != null && dataGuiaRemision.getNumeroBaja() != null && dataGuiaRemision.getNumeroBaja() > 0){
            System.out.println("Entro aqui para anular la guia");
            GuiaRemisionEntity guiaAnulada = guiaRemisionRepository.findByNumeroDocumentoIdentidadRemitenteAndSerieAndNumero(dataGuiaRemision.getRuc(),dataGuiaRemision.getSerieBaja(),dataGuiaRemision.getNumeroBaja());
            ObjectMapper obj = new ObjectMapper();
            System.out.println(obj.writeValueAsString(guiaAnulada));
            guiaAnulada.setEstado("08");
            guiaAnulada.setEstadoEnSunat(EstadoSunatEnum.ANULADO.getAbreviado());

            guiaRemisionRepository.save(guiaAnulada);
        }


        return messageResponse.toString();
    }

    private void comunicacionSuccess(String ruc, String tipoComprobante, Long idTmpVoucher, Long idPaymentVoucher,
                                     String contenidoBase64, String mensajeRespuesta, String estadoComprobante,
                                     String nombreDocumento, String codigosRespuesta) throws Exception {


        RegisterFileUploadEntity responseStorage = uploadFileCdr(ruc, nombreDocumento, tipoComprobante, ConstantesParameter.REGISTRO_STATUS_NUEVO,
                contenidoBase64);

        tmpVoucherSendBillRepository.deleteById(idTmpVoucher);

        PaymentVoucherEntity paymentVoucher = paymentVoucherRepository.findByIdPaymentVoucher(idPaymentVoucher);
        paymentVoucher.setEstado(estadoComprobante);
        paymentVoucher.setEstadoSunat(EstadoSunatEnum.ACEPTADO.getAbreviado());
        paymentVoucher.setMensajeRespuesta(mensajeRespuesta);
        paymentVoucher.setCodigosRespuestaSunat(codigosRespuesta);

        //AGREGANDO ARCHIVO
        if (responseStorage.getIdRegisterFileSend() != null) {
            paymentVoucher.addFile(PaymentVoucherFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(responseStorage.getIdRegisterFileSend()).build())
                    .tipoArchivo(TipoArchivoEnum.CDR)
                    .build());
        }
        paymentVoucherRepository.save(paymentVoucher);

    }

    private RegisterFileUploadEntity uploadFileCdr(String rucEmisor, String nameDocument, String tipoComprobante, String estadoRegistro,
                                                   String fileXMLZipBase64) throws Exception {


        CompanyEntity companyEntity = companyRepository.findByRuc(rucEmisor);
        RegisterFileUploadEntity file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileXMLZipBase64), nameDocument, "cdr", companyEntity);

        return file;
    }

/*********************************************************************************************************************************************/

    /*private RegisterDownloadUploadEntity uploadFilexlsx( String rucEmisor,String nameDocument, String tipoComprobante, String estadoRegistro,
                                                   String fileExcelZipBase64,ByteArrayInputStream stream) {


        CompanyEntity companyEntity = companyRepository.findByRuc(rucEmisor);
        RegisterDownloadUploadEntity file = amazonS3ClientService.uploadDocumentStorage(stream, nameDocument,companyEntity);

        return file;
    }

    private void comunicacionSuccessxlsx(String ruc,String tipoDocumento, Long id,String estadoComprobante,
                                     String nombreDocumento, ByteArrayInputStream stream) {


        RegisterDownloadUploadEntity responseStorage = uploadFilexlsx(ruc,nombreDocumento,tipoDocumento, ConstantesParameter.REGISTRO_STATUS_NUEVO,estadoComprobante,stream);



        DowloadExcelEntity dowloadExcelEntity = excelRepository.findByIdExcelDocument(id);

        //AGREGANDO ARCHIVO
        if (responseStorage.getIdRegisterDownloadSend() != null) {
            dowloadExcelEntity.addFile(DocumentDownloadFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerDownloadUpload(RegisterDownloadUploadEntity.builder().idRegisterDownloadSend(responseStorage.getIdRegisterDownloadSend()).build())
                    .tipoArchivo(TipoArchivoEnum.XLS)
                    .build());
        }
        excelRepository.save(dowloadExcelEntity);

    }*/
    /*********************************************************************************************************************************************/
    @Override
    public Map<String, Object> sendDocumentExcel(String ruc, Long idExcelDocument ,ByteArrayInputStream stream) throws Exception {

        DowloadExcelEntity dowloadExcelEntity = dowloadExcelRepository.findById(idExcelDocument).get();

        Map<String, Object> result = new HashMap<>();

        //comunicacionSuccessxlsx(dowloadExcelEntity.getIdentificador().substring(0,10),dowloadExcelEntity.getTipoDocumento(),idExcelDocument,dowloadExcelEntity.getEstadoDescarga(), dowloadExcelEntity.getIdentificador(), stream);


        result.put(ConstantesParameter.PARAM_BEAN_GET_STATUS_EXL,dowloadExcelEntity);

        return result;
    }
    /*********************************************************************************************************************************************/





}
