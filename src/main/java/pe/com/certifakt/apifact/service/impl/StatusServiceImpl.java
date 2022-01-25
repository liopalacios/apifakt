package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.bean.ResponsePSE;
import pe.com.certifakt.apifact.bean.ResponseSunat;
import pe.com.certifakt.apifact.enums.*;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.DocumentsVoidedRepository;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.repository.SummaryDocumentRepository;
import pe.com.certifakt.apifact.service.AmazonS3ClientService;
import pe.com.certifakt.apifact.service.SendSunatService;
import pe.com.certifakt.apifact.service.StatusService;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;
import pe.com.certifakt.apifact.util.Logger;
import pe.com.certifakt.apifact.util.UtilArchivo;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Service
@AllArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final SummaryDocumentRepository summaryDocumentRepository;
    private final DocumentsVoidedRepository documentsVoidedRepository;
    private final PaymentVoucherRepository paymentVoucherRepository;
    private final AmazonS3ClientService amazonS3ClientService;
    private final SendSunatService sendSunatService;
    private final CompanyRepository companyRepository;


    @Transactional
    @Override
    public ResponsePSE getStatus(String numeroTicket, String tipoResumen,
                                 String userName, String rucEmisor) {

        ResponseSunat respSunat;
        ResponsePSE resp = null;
        OperacionLogEnum operacionLog = null;

        try {
            operacionLog = (tipoResumen.equals(ConstantesSunat.RESUMEN_DIARIO_BOLETAS)) ?
                    OperacionLogEnum.STATUS_SUNAT_SUMMARY : OperacionLogEnum.STATUS_SUNAT_VOIDED;

            resp = new ResponsePSE();
            respSunat = sendSunatService.getStatus(numeroTicket, tipoResumen, rucEmisor);
//LEYTER
            Logger.register(TipoLogEnum.INFO, rucEmisor, tipoResumen, operacionLog,
                    SubOperacionLogEnum.SEND_SUNAT, respSunat.toString());

            switch (respSunat.getEstadoComunicacionSunat()) {
                case SUCCESS:
                    comunicacionSuccess(
                            tipoResumen,
                            numeroTicket,
                            respSunat.getStatusCode(),
                            respSunat.getMessage(),
                            respSunat.getNameDocument(),
                            respSunat.getRucEmisor(),
                            respSunat.getContentBase64(),
                            userName,
                            EstadoComprobanteEnum.ACEPTADO
                    );
                    resp.setEstado(true);
                    resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK);
                    resp.setMensaje("[" + respSunat.getStatusCode() + "] " + respSunat.getMessage());
                    break;
                case SUCCESS_WITH_WARNING:
                    comunicacionSuccess(
                            tipoResumen,
                            numeroTicket,
                            respSunat.getStatusCode(),
                            respSunat.getMessage(),
                            respSunat.getNameDocument(),
                            rucEmisor,
                            respSunat.getContentBase64(),
                            userName,
                            EstadoComprobanteEnum.ACEPTADO_ADVERTENCIA
                    );
                    resp.setEstado(true);
                    resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK);
                    resp.setMensaje("[" + respSunat.getStatusCode() + "] " + respSunat.getMessage());
                    break;
                case SUCCESS_WITH_ERROR_CONTENT:
                    comunicacionError(
                            tipoResumen,
                            numeroTicket,
                            respSunat.getStatusCode(),
                            respSunat.getMessage(),
                            rucEmisor,
                            userName,
                            EstadoComprobanteEnum.ERROR);
                    resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_ERROR);
                    resp.setMensaje("[" + respSunat.getStatusCode() + "] " + respSunat.getMessage());
                    resp.setEstado(false);
                    break;
                case SUCCESS_WITHOUT_CONTENT_CDR:
                    resp.setMensaje(respSunat.getMessage());
                    resp.setEstado(false);
                    break;
                case WITHOUT_CONNECTION:
                case PENDING:
                    comunicacionPendiente(tipoResumen, numeroTicket, resp);
                    resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO);
                    resp.setMensaje(respSunat.getMessage());
                    resp.setEstado(false);
                    break;
                default:
//					resp.setRespuesta(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO);
                    resp.setMensaje(respSunat.getMessage());
                    resp.setEstado(false);
                    break;
            }

        } catch (IOException e) {

            resp.setEstado(false);
            resp.setMensaje(e.getMessage());

            Logger.register(TipoLogEnum.ERROR, rucEmisor, numeroTicket, operacionLog,
                    SubOperacionLogEnum.IN_PROCESS, e.getMessage(), numeroTicket, e);

        } catch (Exception e) {

            resp.setEstado(false);
            resp.setMensaje(e.getMessage());

            Logger.register(TipoLogEnum.ERROR, rucEmisor, numeroTicket, operacionLog,
                    SubOperacionLogEnum.IN_PROCESS, e.getMessage(), numeroTicket, e);

        }

        return resp;
    }


    private void comunicacionPendiente(String tipoDocumentoResumen, String numeroTicket, ResponsePSE responsePSE) {

        if (tipoDocumentoResumen.equals(ConstantesSunat.RESUMEN_DIARIO_BOLETAS)) {
            SummaryDocumentEntity summary = summaryDocumentRepository.getSummaryByTicket(numeroTicket);
            Integer intentos = summary.getIntentosGetStatus() != null ? summary.getIntentosGetStatus() : 0;
            summary.setIntentosGetStatus(intentos + 1);
            summary = summaryDocumentRepository.save(summary);
            responsePSE.setIntentosGetStatus(summary.getIntentosGetStatus());
        } else {
            VoidedDocumentsEntity voided = documentsVoidedRepository.getVoidedByTicket(numeroTicket);
            Integer intentos = voided.getIntentosGetStatus() != null ? voided.getIntentosGetStatus() : 0;
            if (voided.getIntentosGetStatus() == null) voided.setIntentosGetStatus(0);
            voided.setIntentosGetStatus(intentos + 1);
            voided = documentsVoidedRepository.save(voided);
            responsePSE.setIntentosGetStatus(voided.getIntentosGetStatus());
        }
    }


    public void actualizarDocumentoResumenByTicket(Map<String, String> params,
                                                   String tipoDocumento, Long idRegisterFile, EstadoComprobanteEnum estadoComprobanteEnum) {

        Timestamp fechaModificacion = new Timestamp(Calendar.getInstance().getTime().getTime());
        String numeroTicket = params.get(ConstantesParameter.PARAM_NUM_TICKET);
        String estado = params.get(ConstantesParameter.PARAM_ESTADO);
        String codeResponse = params.get(ConstantesParameter.PARAM_RESPONSE_CODE);
        String description = params.get(ConstantesParameter.PARAM_DESCRIPTION);
        String usuario = params.get(ConstantesParameter.PARAM_USER_NAME);

        List<String> identificadoresComprobantes;
        List<String> comprobantesByAnular = null;
        List<String> comprobantesByAceptar = null;
        String rucEmisor;
        StringBuilder msgLog = new StringBuilder();

        if (tipoDocumento.equals(ConstantesSunat.RESUMEN_DIARIO_BOLETAS)) {

            SummaryDocumentEntity summary;
            summary = summaryDocumentRepository.getSummaryByTicket(numeroTicket);
            rucEmisor = summary.getRucEmisor();

            summary.setEstado(estado);
            summary.setCodigoRespuesta(codeResponse);
            summary.setDescripcionRespuesta(description);
            summary.setUserNameModify(usuario);
            summary.setFechaModificacion(fechaModificacion);
            summary.setEstadoComprobante(estadoComprobanteEnum.getCodigo());

            //AGREGANDO ARCHIVO
            if (idRegisterFile != null) {
                summary.addFile(SummaryFileEntity.builder()
                        .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                        .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(idRegisterFile).build())
                        .tipoArchivo(TipoArchivoEnum.CDR)
                        .build());
            }

            summaryDocumentRepository.save(summary);

            msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                    append("{numeroTicket:").append(numeroTicket).append("}{estado:").append(estado).
                    append("}{codeResponse:").append(codeResponse).append("}{description:").append(description).
                    append("}{fechaModificacion:").append(fechaModificacion).append("}{estadoComprobante:").
                    append(estadoComprobanteEnum.getCodigo()).append("}");

            Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, OperacionLogEnum.STATUS_SUNAT_SUMMARY,
                    SubOperacionLogEnum.UPDATE_BD_SUMMARY, msgLog.toString());
            comprobantesByAnular = new ArrayList<>();
            comprobantesByAceptar = new ArrayList<>();

            for (DetailDocsSummaryEntity detail : summary.getDetailDocsSummaries()) {

                if (detail.getEstadoItem() == ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION ||
                        detail.getEstadoItem() == ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION) {
                    comprobantesByAceptar.add(rucEmisor + "-" + detail.getTipoComprobante() + "-"
                            + detail.getSerieDocumento() + "-" + detail.getNumeroDocumento());
                } else {
                    comprobantesByAnular.add(rucEmisor + "-" + detail.getTipoComprobante() + "-"
                            + detail.getSerieDocumento() + "-" + detail.getNumeroDocumento());
                }
            }

            identificadoresComprobantes = new ArrayList<String>(comprobantesByAceptar);
            identificadoresComprobantes.addAll(comprobantesByAnular);

        } else {

            VoidedDocumentsEntity voided;
            identificadoresComprobantes = new ArrayList<>();
            voided = documentsVoidedRepository.getVoidedByTicket(numeroTicket);
            rucEmisor = voided.getRucEmisor();

            voided.setEstado(estado);
            voided.setCodigoRespuesta(codeResponse);
            voided.setDescripcionRespuesta(description);
            voided.setUserNameModify(usuario);
            voided.setFechaModificacion(fechaModificacion);
            voided.setEstadoComprobante(estadoComprobanteEnum.getCodigo());

            //AGREGANDO ARCHIVO
            if (idRegisterFile != null) {
                voided.addFile(VoidedFileEntity.builder()
                        .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                        .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(idRegisterFile).build())
                        .tipoArchivo(TipoArchivoEnum.CDR)
                        .build());
            }

            documentsVoidedRepository.save(voided);

            msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                    append("{numeroTicket:").append(numeroTicket).append("}{estado:").append(estado).
                    append("}{codeResponse:").append(codeResponse).append("}{description:").append(description).
                    append("}{fechaModificacion:").append(fechaModificacion).append("}{estadoComprobante:").
                    append(estadoComprobanteEnum.getCodigo()).append("}");

            Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, OperacionLogEnum.STATUS_SUNAT_VOIDED,
                    SubOperacionLogEnum.UPDATE_BD_VOIDED, msgLog.toString());

            for (DetailDocsVoidedEntity detail : voided.getBajaDocumentos()) {
                identificadoresComprobantes.add(rucEmisor + "-" + detail.getTipoComprobante() + "-" +
                        detail.getSerieDocumento() + "-" + detail.getNumeroDocumento());
            }
        }

        switch (estadoComprobanteEnum) {
            case ACEPTADO:
            case ACEPTADO_ADVERTENCIA:
                if (tipoDocumento.equals(ConstantesSunat.COMUNICACION_BAJA)) {
                    paymentVoucherRepository.updateComprobantesByBajaDocumentos(
                            identificadoresComprobantes, usuario, fechaModificacion);

                    msgLog.setLength(0);
                    msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                            append("{identificadoresComprobantes:").append(identificadoresComprobantes).
                            append("}{fechaModificacion:").append(fechaModificacion).append("}");

                    Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, OperacionLogEnum.STATUS_SUNAT_VOIDED,
                            SubOperacionLogEnum.UPDATE_BD_PAYMENT_VOUCHER, msgLog.toString());
                } else {

                    if (!comprobantesByAceptar.isEmpty()) {
                        paymentVoucherRepository.updateComprobantesBySummaryDocuments(
                                comprobantesByAceptar,
                                EstadoComprobanteEnum.ACEPTADO.getCodigo(),
                                EstadoSunatEnum.ACEPTADO.getAbreviado(),
                                usuario,
                                fechaModificacion);

                        msgLog.setLength(0);
                        msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                                append("{comprobantesByAceptar:").append(comprobantesByAceptar).
                                append("}{EstadoComprobanteEnum.ACEPTADO:").append(EstadoComprobanteEnum.ACEPTADO.getCodigo()).
                                append("}{EstadoSunatEnum.ACEPTADO").append(EstadoSunatEnum.ACEPTADO.getAbreviado()).
                                append("}{fechaModificacion").append(fechaModificacion).append("}");

                        Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, OperacionLogEnum.STATUS_SUNAT_SUMMARY,
                                SubOperacionLogEnum.UPDATE_BD_PAYMENT_VOUCHER, msgLog.toString());
                    }
                    if (!comprobantesByAnular.isEmpty()) {
                        paymentVoucherRepository.updateComprobantesBySummaryDocuments(
                                comprobantesByAnular,
                                EstadoComprobanteEnum.ANULADO.getCodigo(),
                                EstadoSunatEnum.ANULADO.getAbreviado(),
                                usuario, fechaModificacion);

                        msgLog.setLength(0);
                        msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                                append("{comprobantesByAnular:").append(comprobantesByAceptar).
                                append("}{EstadoComprobanteEnum.ANULADO:").append(EstadoComprobanteEnum.ANULADO.getCodigo()).
                                append("}{EstadoSunatEnum.ANULADO").append(EstadoSunatEnum.ANULADO.getAbreviado()).
                                append("}{fechaModificacion").append(fechaModificacion).append("}");

                        Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, OperacionLogEnum.STATUS_SUNAT_SUMMARY,
                                SubOperacionLogEnum.UPDATE_BD_PAYMENT_VOUCHER, msgLog.toString());
                    }
                }
                break;
            case ERROR:
                paymentVoucherRepository.updateComprobantesOnResumenError(
                        identificadoresComprobantes, usuario, fechaModificacion);

                msgLog.setLength(0);
                msgLog.append("{").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("}").
                        append("{identificadoresComprobantesRechazados:").append(identificadoresComprobantes).
                        append("}{fechaModificacion").append(fechaModificacion).append("}");

                Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, OperacionLogEnum.STATUS_SUNAT_SUMMARY,
                        SubOperacionLogEnum.UPDATE_BD_PAYMENT_VOUCHER, msgLog.toString());
                break;
            default:
                break;
        }
    }

    private void comunicacionSuccess(String tipoDocumentoResumen, String numeroTicket,
                                     String codeResponse, String messageResponse, String nameDocument,
                                     String rucEmisor, String fileBase64, String userName, EstadoComprobanteEnum aceptado) throws Exception {

        Map<String, String> params = new HashMap<>();
        String estadoDocumentInBD;
        OperacionLogEnum operacionLog = (tipoDocumentoResumen.equals(ConstantesSunat.RESUMEN_DIARIO_BOLETAS)) ?
                OperacionLogEnum.STATUS_SUNAT_SUMMARY : OperacionLogEnum.STATUS_SUNAT_VOIDED;

        estadoDocumentInBD = getEstadoDocumentoResumenInBD(tipoDocumentoResumen, numeroTicket);
        if (estadoDocumentInBD.equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO)) {

            CompanyEntity companyEntity = companyRepository.findByRuc(rucEmisor);
            RegisterFileUploadEntity file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileBase64), nameDocument, "summary", companyEntity);
            Logger.register(TipoLogEnum.INFO, rucEmisor, numeroTicket, operacionLog,
                    SubOperacionLogEnum.STORAGE_FILE, "{" + ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "}{" + params + "}");

            params.put(ConstantesParameter.PARAM_NUM_TICKET, numeroTicket);
            params.put(ConstantesParameter.PARAM_ESTADO, ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK);
            params.put(ConstantesParameter.PARAM_RESPONSE_CODE, codeResponse);
            params.put(ConstantesParameter.PARAM_DESCRIPTION, messageResponse);
            params.put(ConstantesParameter.PARAM_USER_NAME, userName);

            actualizarDocumentoResumenByTicket(params, tipoDocumentoResumen, file.getIdRegisterFileSend(), aceptado);

            if (tipoDocumentoResumen.equals(ConstantesSunat.RESUMEN_DIARIO_BOLETAS)) {
                try {
                    SummaryDocumentEntity summaryDocumentEntity = summaryDocumentRepository.getSummaryByTicket(numeroTicket);
                    if (summaryDocumentEntity != null) {
                        String finalRucEmisor = rucEmisor;
                        summaryDocumentEntity.getDetailDocsSummaries().forEach(detailDocsSummaryEntity -> {
                            PaymentVoucherEntity paymentVoucherEntity = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(finalRucEmisor, detailDocsSummaryEntity.getTipoComprobante(), detailDocsSummaryEntity.getSerieDocumento(), detailDocsSummaryEntity.getNumeroDocumento());
                            if (paymentVoucherEntity != null) {

                                /*enviarCorreoProducer.produceEnviarCorreo(
                                        paymentVoucherEntity.getIdPaymentVoucher(),
                                        finalRucEmisor,
                                        paymentVoucherEntity.getIdentificadorDocumento());*/
                                //TODO COLA CORREO
                                Logger.register(TipoLogEnum.INFO, finalRucEmisor, paymentVoucherEntity.getIdentificadorDocumento(),
                                        operacionLog, SubOperacionLogEnum.SEND_EMAIL,
                                        "{" + ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "}{IdPaymentVoucher:" +
                                                paymentVoucherEntity.getIdPaymentVoucher() + "}{ticket:" + numeroTicket + "}");
                            }
                        });
                    }
                } catch (Exception e) {

                    Logger.register(TipoLogEnum.ERROR, rucEmisor, numeroTicket, operacionLog,
                            SubOperacionLogEnum.SEND_QUEUE, e.getMessage(), numeroTicket, e);

                }
            }
        }
    }

    private void comunicacionError(String tipoDocumentoResumen, String numeroTicket,
                                   String codeResponse, String messageResponse, String rucEmisor,
                                   String userName, EstadoComprobanteEnum estadoComprobanteError) throws Exception {

        Map<String, String> params;
        String estadoDocumentInBD;

        estadoDocumentInBD = getEstadoDocumentoResumenInBD(tipoDocumentoResumen, numeroTicket);
        if (estadoDocumentInBD.equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO)) {

            params = new HashMap<>();

            params.put(ConstantesParameter.PARAM_NUM_TICKET, numeroTicket);
            params.put(ConstantesParameter.PARAM_ESTADO, ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_ERROR);
            params.put(ConstantesParameter.PARAM_RESPONSE_CODE, codeResponse);
            params.put(ConstantesParameter.PARAM_DESCRIPTION, messageResponse);
            params.put(ConstantesParameter.PARAM_USER_NAME, userName);

            actualizarDocumentoResumenByTicket(params, tipoDocumentoResumen, null, estadoComprobanteError);
        }
    }

    @Override
    public String getEstadoDocumentoResumenInBD(String tipoDocumento, String numeroTicket) {

        String estado;

        if (tipoDocumento.equals(ConstantesSunat.RESUMEN_DIARIO_BOLETAS)) {
            estado = summaryDocumentRepository.getEstadoByNumeroTicket(numeroTicket);
        } else {
            estado = documentsVoidedRepository.getEstadoByNumeroTicket(numeroTicket);
        }

        return estado;
    }

}
