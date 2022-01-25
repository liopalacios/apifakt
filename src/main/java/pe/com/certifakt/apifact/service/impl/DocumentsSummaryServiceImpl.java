package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.dto.inter.OseInterDto;
import pe.com.certifakt.apifact.enums.*;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.exception.SignedException;
import pe.com.certifakt.apifact.exception.TemplateException;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.repository.SummaryDocumentRepository;
import pe.com.certifakt.apifact.service.*;
import pe.com.certifakt.apifact.util.*;

import java.sql.Timestamp;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentsSummaryServiceImpl implements DocumentsSummaryService {

    @Autowired
    private CompanyRepository companyRepository;

    private final SummaryDocumentRepository summaryDocumentRepository;
    private final PaymentVoucherRepository paymentVoucherRepository;
    private final TemplateService templateService;
    private final SendSunatService sendSunat;
    private final StatusService statusService;
    private final AmazonS3ClientService amazonS3ClientService;
    private final PaymentVoucherService paymentVoucherService;


    /**
     * Este metodo registra el resumen de boleta por fecha de emision, enviada a la sunat
     * Observacion:
     * Fecha de generacion podria ser mayor o igual a la fecha de emision
     * actualmente la sunat no esta validando correctamente, por lo cual
     * como solucion temporal se esta colocando la misma fecha
     */
    @Override
    public void registrarSummaryDocuments(Summary summary, Long idRegisterFile, String usuario, String ticket, List<Long> ids) {

        Date fechaActual = Calendar.getInstance().getTime();
        Timestamp fechaEjecucion = new Timestamp(fechaActual.getTime());
        SummaryDocumentEntity summaryEntity = new SummaryDocumentEntity();

        summaryEntity.setCorrelativoDia(summary.getNroResumenDelDia());
        summaryEntity.setEstado(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO);
        summaryEntity.setFechaEmision(summary.getFechaEmision());
        summaryEntity.setFechaGeneracion(UtilFormat.fecha(fechaActual, "yyyy-MM-dd"));
        summaryEntity.setFechaGeneracionResumen(fechaEjecucion);
        summaryEntity.setIdDocument(summary.getId());
        summaryEntity.setTicketSunat(ticket);
        summaryEntity.setUserName(usuario);
        summaryEntity.setRucEmisor(summary.getRucEmisor());
        summaryEntity.setEstadoComprobante(summary.getEstadoComprobante());

        //AGREGANDO ARCHIVO
        if (idRegisterFile != null) {
            summaryEntity.addFile(SummaryFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(idRegisterFile).build())
                    .tipoArchivo(TipoArchivoEnum.XML)
                    .build());
        }


        for (SummaryDetail item : summary.getItems()) {

            DetailDocsSummaryEntity detail = new DetailDocsSummaryEntity();
            detail.setEstado(ConstantesParameter.REGISTRO_ACTIVO);
            detail.setEstadoItem(item.getStatusItem());

            detail.setNumeroItem(item.getNumeroItem());
            detail.setSerieDocumento(item.getSerie());
            detail.setNumeroDocumento(item.getNumero());
            detail.setTipoComprobante(item.getTipoComprobante());
            detail.setNumDocReceptor(item.getNumeroDocumentoReceptor());
            detail.setTipoDocIdentReceptor(item.getTipoDocumentoReceptor());
            detail.setSerieAfectado(item.getSerieAfectado());
            detail.setNumeroAfectado(item.getNumeroAfectado());
            detail.setTipoComprobanteAfectado(item.getTipoComprobanteAfectado());
            detail.setImporteTotalVenta(item.getImporteTotalVenta());
            detail.setSumatoriaOtrosCargos(item.getSumatoriaOtrosCargos());

            detail.setTotalValorVentaOperacionExonerado(item.getTotalValorVentaOperacionExonerado());
            detail.setTotalValorVentaOperacionExportacion(item.getTotalValorVentaOperacionExportacion());
            detail.setTotalValorVentaOperacionGratuita(item.getTotalValorVentaOperacionGratuita());
            detail.setTotalValorVentaOperacionGravada(item.getTotalValorVentaOperacionGravada());
            detail.setTotalValorVentaOperacionInafecta(item.getTotalValorVentaOperacionInafecta());

            detail.setTotalIGV(item.getTotalIGV());
            detail.setTotalISC(item.getTotalISC());
            detail.setTotalOtrosTributos(item.getTotalOtrosTributos());

            summaryEntity.addDetailDocsSummary(detail);
        }

        summaryDocumentRepository.save(summaryEntity);
        paymentVoucherRepository.updateStateToSendSunatForSummaryDocuments(ids, usuario, fechaEjecucion);

    }
    private Map<String, String> getTemplateGenerated(String rucEmisor, Summary summary) throws SignedException, TemplateException {


        OseInterDto ose = companyRepository.findOseByRucInter(rucEmisor);
        if (ose != null && ose.getId()==1) {
            return templateService.buildSummaryDailySignOse(summary);
        } else {
            return templateService.buildSummaryDailySign(summary);
        }



    }
    @Transactional
    @Override
    public ResponsePSE generarSummaryByFechaEmisionAndRuc(String rucEmisor, String fechaEmision, IdentificadorComprobante comprobante,
                                                          String usuario) {

        ResponsePSE responsePSE = new ResponsePSE();
        Map<String, String> templateGenerated;
        Map<String, String> params;
        Map<String, Object> resultGetSummary;
        ResponseSunat responseSunat;
        String nameDocumentComplete;
        List<Long> ids;
        String fileXMLZipBase64;
        String nameDocument = null;
        responsePSE.setEstado(false);
        StringBuilder messageBuilder = new StringBuilder();
        Summary summary;

        try {
            resultGetSummary = paymentVoucherService.getSummaryDocumentsByFechaEmision(fechaEmision, rucEmisor, comprobante);
            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.GENERATE_BEAN,
                    resultGetSummary.toString());

            if (resultGetSummary.get(ConstantesParameter.PARAM_BEAN_SUMMARY) != null) {
                summary = (Summary) resultGetSummary.get(ConstantesParameter.PARAM_BEAN_SUMMARY);
                ids = (List<Long>) resultGetSummary.get(ConstantesParameter.PARAM_LIST_IDS);
            } else {
                responsePSE.setEstado(false);
                responsePSE.setMensaje(ConstantesParameter.MSG_SUMMARY_VACIO +
                        "RUC:[" + rucEmisor + "], Fecha emision:[" + fechaEmision + "]");
                responsePSE.setRespuesta(null);
                return responsePSE;
            }

            templateGenerated = getTemplateGenerated(rucEmisor,summary);

            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.GENERATE_TEMPLATE,
                    ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            fileXMLZipBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
            nameDocument = templateGenerated.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
            nameDocumentComplete = nameDocument + "." + ConstantesParameter.TYPE_FILE_ZIP;

            responseSunat = sendSunat.sendSummary(nameDocumentComplete, fileXMLZipBase64, rucEmisor);

            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.SEND_SUNAT,
                    responseSunat.toString());

            messageBuilder.append("[").append(rucEmisor).append("]");
            messageBuilder.append("[").append(fechaEmision).append("]");

            if (responseSunat.getEstadoComunicacionSunat() == null) {
                throw new ServiceException("Error al comunicarse con Sunat");
            }

            switch (responseSunat.getEstadoComunicacionSunat()) {
                case SUCCESS_WITH_ERROR_CONTENT:
                    messageBuilder.append("[").append(responseSunat.getStatusCode()).append("]");
                    messageBuilder.append(responseSunat.getMessage());
                    break;
                case WITHOUT_CONNECTION:
                case ERROR_INTERNO_WS_API:
                    messageBuilder.append(responseSunat.getMessage());
                    break;
                default:
            }

            if (!responseSunat.isSuccess()) {
                throw new Exception(messageBuilder.toString());
            }

            params = new HashMap<String, String>();
            params.put(ConstantesParameter.PARAM_RUC_EMISOR, rucEmisor);
            params.put(ConstantesParameter.PARAM_NAME_DOCUMENT, nameDocument);
            params.put(ConstantesParameter.PARAM_TIPO_ARCHIVO, ConstantesSunat.RESUMEN_DIARIO_BOLETAS);
            params.put(ConstantesParameter.PARAM_STATUS_REGISTRO,
                    ConstantesParameter.REGISTRO_STATUS_NUEVO);

            CompanyEntity companyEntity = companyRepository.findByRuc(rucEmisor);
            RegisterFileUploadEntity file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileXMLZipBase64), nameDocument, "summary", companyEntity);

            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.STORAGE_FILE,
                    params.toString());


            summary.setEstadoComprobante(EstadoComprobanteEnum.PROCESO_ENVIO.getCodigo());

            registrarSummaryDocuments(
                    summary,
                    file.getIdRegisterFileSend(),
                    usuario,
                    responseSunat.getTicket(),
                    ids);
            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.INSERT_BD,
                    ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            responsePSE.setEstado(true);
            responsePSE.setMensaje(messageBuilder.toString() + ConstantesParameter.MSG_RESP_OK);
            responsePSE.setRespuesta(responseSunat.getMessage());
            responsePSE.setTicket(responseSunat.getTicket());

        } catch (TemplateException | SignedException ex) {

            responsePSE.setMensaje(ex.getMessage());
            responsePSE.setRespuesta("RUC:[" + rucEmisor + "], Fecha emision:[" + fechaEmision + "]");

            Logger.register(TipoLogEnum.ERROR, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.GENERATE_TEMPLATE,
                    ex.getMessage(), responsePSE.getRespuesta().toString(), ex);

        } catch (Exception ex) {

            responsePSE.setMensaje(ex.getMessage());
            responsePSE.setRespuesta("RUC:[" + rucEmisor + "], Fecha emision:[" + fechaEmision + "]");

            Logger.register(TipoLogEnum.ERROR, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.IN_PROCESS,
                    ex.getMessage(), responsePSE.getRespuesta().toString(), ex);

        }

        Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision, OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS,
                SubOperacionLogEnum.COMPLETED, responsePSE.toString());

        return responsePSE;
    }
    @Transactional
    @Override
    public ResponsePSE generarSummaryNotaCreditoByFechaEmisionAndRuc(String rucEmisor, String fechaEmision, IdentificadorComprobante comprobante,
                                                                     String usuario) {

        ResponsePSE responsePSE = new ResponsePSE();
        Map<String, String> templateGenerated;
        Map<String, String> params;
        Map<String, Object> resultGetSummary;
        ResponseSunat responseSunat;
        String nameDocumentComplete;
        List<Long> ids;
        String fileXMLZipBase64;
        String nameDocument = null;
        responsePSE.setEstado(false);
        StringBuilder messageBuilder = new StringBuilder();
        Summary summary;

        try {
            resultGetSummary = paymentVoucherService.getSummaryNotaCreditoDocumentsByFechaEmision(fechaEmision, rucEmisor, comprobante);
            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.GENERATE_BEAN,
                    resultGetSummary.toString());

            if (resultGetSummary.get(ConstantesParameter.PARAM_BEAN_SUMMARY) != null) {
                summary = (Summary) resultGetSummary.get(ConstantesParameter.PARAM_BEAN_SUMMARY);
                ids = (List<Long>) resultGetSummary.get(ConstantesParameter.PARAM_LIST_IDS);
            } else {
                responsePSE.setEstado(false);
                responsePSE.setMensaje(ConstantesParameter.MSG_SUMMARY_VACIO +
                        "RUC:[" + rucEmisor + "], Fecha emision:[" + fechaEmision + "]");
                responsePSE.setRespuesta(null);
                return responsePSE;
            }

            templateGenerated = getTemplateGenerated(rucEmisor,summary);

            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.GENERATE_TEMPLATE,
                    ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            fileXMLZipBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
            nameDocument = templateGenerated.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
            nameDocumentComplete = nameDocument + "." + ConstantesParameter.TYPE_FILE_ZIP;

            responseSunat = sendSunat.sendSummary(nameDocumentComplete, fileXMLZipBase64, rucEmisor);

            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.SEND_SUNAT,
                    responseSunat.toString());

            messageBuilder.append("[").append(rucEmisor).append("]");
            messageBuilder.append("[").append(fechaEmision).append("]");

            if (responseSunat.getEstadoComunicacionSunat() == null) {
                throw new ServiceException("Error al comunicarse con Sunat");
            }

            switch (responseSunat.getEstadoComunicacionSunat()) {
                case SUCCESS_WITH_ERROR_CONTENT:
                    messageBuilder.append("[").append(responseSunat.getStatusCode()).append("]");
                    messageBuilder.append(responseSunat.getMessage());
                    break;
                case WITHOUT_CONNECTION:
                case ERROR_INTERNO_WS_API:
                    messageBuilder.append(responseSunat.getMessage());
                    break;
                default:
            }

            if (!responseSunat.isSuccess()) {
                throw new Exception(messageBuilder.toString());
            }

            params = new HashMap<String, String>();
            params.put(ConstantesParameter.PARAM_RUC_EMISOR, rucEmisor);
            params.put(ConstantesParameter.PARAM_NAME_DOCUMENT, nameDocument);
            params.put(ConstantesParameter.PARAM_TIPO_ARCHIVO, ConstantesSunat.RESUMEN_DIARIO_BOLETAS);
            params.put(ConstantesParameter.PARAM_STATUS_REGISTRO,
                    ConstantesParameter.REGISTRO_STATUS_NUEVO);

            CompanyEntity companyEntity = companyRepository.findByRuc(rucEmisor);
            RegisterFileUploadEntity file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileXMLZipBase64), nameDocument, "summary", companyEntity);

            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.STORAGE_FILE,
                    params.toString());


            summary.setEstadoComprobante(EstadoComprobanteEnum.PROCESO_ENVIO.getCodigo());

            registrarSummaryDocuments(
                    summary,
                    file.getIdRegisterFileSend(),
                    usuario,
                    responseSunat.getTicket(),
                    ids);
            Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.INSERT_BD,
                    ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

            responsePSE.setEstado(true);
            responsePSE.setMensaje(messageBuilder.toString() + ConstantesParameter.MSG_RESP_OK);
            responsePSE.setRespuesta(responseSunat.getTicket());
            responsePSE.setTicket(responseSunat.getTicket());

        } catch (TemplateException | SignedException ex) {

            responsePSE.setMensaje(ex.getMessage());
            responsePSE.setRespuesta("RUC:[" + rucEmisor + "], Fecha emision:[" + fechaEmision + "]");

            Logger.register(TipoLogEnum.ERROR, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.GENERATE_TEMPLATE,
                    ex.getMessage(), responsePSE.getRespuesta().toString(), ex);

        } catch (Exception ex) {

            responsePSE.setMensaje(ex.getMessage());
            responsePSE.setRespuesta("RUC:[" + rucEmisor + "], Fecha emision:[" + fechaEmision + "]");

            Logger.register(TipoLogEnum.ERROR, rucEmisor, fechaEmision,
                    OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS, SubOperacionLogEnum.IN_PROCESS,
                    ex.getMessage(), responsePSE.getRespuesta().toString(), ex);

        }

        Logger.register(TipoLogEnum.INFO, rucEmisor, fechaEmision, OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS,
                SubOperacionLogEnum.COMPLETED, responsePSE.toString());

        return responsePSE;
    }

    @Override
    public List<SummaryResponse> listarSummariesByFechaGeneracion(String fechaGeneracion) {

        List<SummaryResponse> response = new ArrayList<>();
        List<SummaryDocumentEntity> summaries = summaryDocumentRepository.getSummariesByFechaGeneracion(fechaGeneracion);

        if (summaries == null || summaries.isEmpty()) {
            return response;
        }

        for (SummaryDocumentEntity summary : summaries) {

            SummaryResponse summaryResponse = new SummaryResponse();
            String estadoSummary;

            summaryResponse.setFechaEmision(summary.getFechaEmision());
            summaryResponse.setTicket(summary.getTicketSunat());
            summaryResponse.setIdDocumento(summary.getIdDocument());
            estadoSummary = summary.getEstado();
            List<Comprobante> comprobantes = new ArrayList<>();
            for (DetailDocsSummaryEntity detail : summary.getDetailDocsSummaries()) {

                Comprobante comprobante = new Comprobante();
                comprobante.setSerie(detail.getSerieDocumento());
                comprobante.setNumero(detail.getNumeroDocumento());
                comprobante.setTipoComprobante(detail.getTipoComprobante());
                EstadoComprobanteEnum estado = null;

                switch (estadoSummary) {
                    case ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK:
                        if (detail.getEstadoItem().equals(ConstantesParameter.STATE_ITEM_PENDIENTE_ADICION)
                                || detail.getEstadoItem().equals(ConstantesParameter.STATE_ITEM_PENDIENTE_MODIFICACION)) {
                            estado = EstadoComprobanteEnum.ACEPTADO;
                        } else {
                            estado = EstadoComprobanteEnum.ANULADO;
                        }
                        break;
                    case ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_ERROR:
                        estado = EstadoComprobanteEnum.RECHAZADO;
                        break;
                    default:
                        // FIXME VALIDARRRRR POR CAMBIO EN ESTADOS
//						estado = EstadoComprobanteEnum.ENVIADO;
                }
                comprobante.setEstado(estado.getDescripcion());
                comprobantes.add(comprobante);
            }
            summaryResponse.setComprobantes(comprobantes);
            response.add(summaryResponse);
        }

        return response;
    }

    @Override
    public ResponsePSE processSummaryTicket(String ticket, String useName, String rucEmisor) {

        ResponsePSE responsePSE = null;
        List<Object[]> data;

        String estado = null;
        data = summaryDocumentRepository.getEstadoAndRucEmisorByNumeroTicket(ticket);
        Logger.register(TipoLogEnum.INFO, "-", ticket, OperacionLogEnum.STATUS_SUNAT_SUMMARY,
                SubOperacionLogEnum.SELECT_BD, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "[" + data.toString() + "]");

        Object[] dataReg = data.get(0);
        rucEmisor = (String) dataReg[0];
        estado = (String) dataReg[1];

        if (estado.equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO)) {
            responsePSE = statusService.getStatus(ticket, ConstantesSunat.RESUMEN_DIARIO_BOLETAS, useName, rucEmisor);

        } else {
            responsePSE = new ResponsePSE();
            responsePSE.setRespuesta(estado);
            responsePSE.setEstado(false);
            responsePSE.setMensaje("El ticket[" + ticket + "] ya ha sido procesado y se encuentra en estado[" + estado + "]");
        }

        Logger.register(TipoLogEnum.INFO, rucEmisor, ticket, OperacionLogEnum.STATUS_SUNAT_SUMMARY,
                SubOperacionLogEnum.COMPLETED, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "[" + responsePSE.toString() + "]");
        return responsePSE;
    }

    @Override
    public List<String> getFechasPendientesNotas() {
        return paymentVoucherService.getFechasPendientesNotas();
    }

    @Override
    public List<String> getRucsPendientesNotas() {
        return paymentVoucherService.getRucsPendientesNotas();
    }
}
