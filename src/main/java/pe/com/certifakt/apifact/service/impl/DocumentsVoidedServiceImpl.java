package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.enums.*;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.jms.MessageProducer;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.DocumentsVoidedRepository;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.service.*;
import pe.com.certifakt.apifact.util.*;
import pe.com.certifakt.apifact.validate.SummaryValidate;
import pe.com.certifakt.apifact.validate.VoucherAnnularValidate;

import java.sql.Timestamp;
import java.util.*;

@Service
@AllArgsConstructor
public class DocumentsVoidedServiceImpl implements DocumentsVoidedService {

    private final DocumentsVoidedRepository documentsVoidedRepository;
    private final PaymentVoucherRepository paymentVoucherRepository;
    private final AmazonS3ClientService amazonS3ClientService;
    private final CompanyRepository companyRepository;
    private final TemplateService templateService;
    private final StatusService statusService;
    private final SendSunatService sendSunat;
    private final VoucherAnnularValidate anularValidate;
    private final SummaryValidate validate;
    private final MessageProducer messageProducer;
    private final DocumentsSummaryService documentsSummaryService;

    @Override
    public VoidedDocumentsEntity registrarVoidedDocuments(Voided voided, Long idRegisterFile,
                                                          String usuario, String ticket) {

        Date fechaActual = Calendar.getInstance().getTime();
        Timestamp fechaEjecucion = new Timestamp(fechaActual.getTime());
        VoidedDocumentsEntity documentSummary = new VoidedDocumentsEntity();
        List<String> identificadorComprobantes = new ArrayList<>();

        documentSummary.setEstado(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO);
        documentSummary.setFechaBajaDocs(voided.getFechaBaja());
        documentSummary.setFechaGeneracionBaja(voided.getFechaGeneracion());
        documentSummary.setCorrelativoGeneracionDia(voided.getCorrelativoGeneracionDia());
        documentSummary.setIdDocument(voided.getId());
        documentSummary.setRucEmisor(voided.getRucEmisor());
        documentSummary.setTicketSunat(ticket);
        documentSummary.setFechaGeneracionResumen(fechaEjecucion);
        documentSummary.setUserName(usuario);
        documentSummary.setEstadoComprobante(voided.getEstadoComprobante());

        //AGREGANDO ARCHIVO
        if (idRegisterFile != null) {
            documentSummary.addFile(VoidedFileEntity.builder()
                    .estadoArchivo(EstadoArchivoEnum.ACTIVO)
                    .registerFileUpload(RegisterFileUploadEntity.builder().idRegisterFileSend(idRegisterFile).build())
                    .tipoArchivo(TipoArchivoEnum.XML)
                    .build());
        }

        for (VoidedLine item : voided.getLines()) {

            DetailDocsVoidedEntity detail = new DetailDocsVoidedEntity();

            detail.setSerieDocumento(item.getSerieDocumento());
            detail.setNumeroDocumento(item.getNumeroDocumento());
            detail.setTipoComprobante(item.getTipoComprobante());
            detail.setEstado(ConstantesParameter.REGISTRO_ACTIVO);
            detail.setMotivoBaja(item.getRazon());
            detail.setNumeroItem(item.getNumeroItem());
            documentSummary.addDetailDocsVoided(detail);

            identificadorComprobantes.add(voided.getRucEmisor() + "-" + item.getTipoComprobante() + "-" +
                    item.getSerieDocumento() + "-" + item.getNumeroDocumento());
        }

        documentSummary = documentsVoidedRepository.save(documentSummary);

        Logger.register(TipoLogEnum.INFO, voided.getRucEmisor(), voided.getId(), OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                SubOperacionLogEnum.INSERT_BD_VOIDED_DOCUMENTS, "{" + ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "}{" +
                        documentSummary.toString() + "}");

        paymentVoucherRepository.updateStateToSendSunatForVoidedDocuments(
                identificadorComprobantes,
                EstadoComprobanteEnum.PENDIENTE_ANULACION.getCodigo(),
                usuario,
                fechaEjecucion);

        Logger.register(TipoLogEnum.INFO, voided.getRucEmisor(), voided.getId(), OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                SubOperacionLogEnum.UPDATE_BD_PAYMENT_VOUCHER, "{" + ConstantesParameter.MSG_RESP_SUB_PROCESO_OK +
                        "}{identificadorComprobantes:" + identificadorComprobantes + "}{EstadoComprobante:"
                        + EstadoComprobanteEnum.PENDIENTE_ANULACION.getCodigo() + "}{fechaEjecucion:" + fechaEjecucion + "}");

        return documentSummary;
    }

    @Override
    public String obtenerEstadoSummaryByNumeroTicket(String numeroTicket) {

        String estado;
        estado = documentsVoidedRepository.getEstadoByNumeroTicket(numeroTicket);

        return estado;
    }

    @Override
    public List<Comprobante> listarIdentificadorDocumentoByTicket(String ticket) {

        List<Comprobante> response = new ArrayList<>();
        VoidedDocumentsEntity voided = documentsVoidedRepository.getVoidedByTicket(ticket);

        if (voided == null) {
            return response;
        }

        for (DetailDocsVoidedEntity detail : voided.getBajaDocumentos()) {
            Comprobante identificador = new Comprobante();
            identificador.setSerie(detail.getSerieDocumento());
            identificador.setNumero(detail.getNumeroDocumento());
            identificador.setTipoComprobante(detail.getTipoComprobante());
            response.add(identificador);
        }

        return response;
    }

    public Map<String, String> annularDocumentSendVoidedDocuments(Voided voided, String userName) throws Exception {

        ResponseSunat responseSunat;
        String nameDocumentComplete;
        String fileXMLZipBase64;
        String nameDocument = null;
        Map<String, String> params;
        Map<String, String> resp;
        Map<String, String> templateGenerated;
        StringBuilder messageBuilder = new StringBuilder();

        CompanyEntity companyEntity = completarDatosVoided(voided);
        templateGenerated = templateService.buildVoidedDocumentsSign(voided);
        Logger.register(TipoLogEnum.INFO, voided.getRucEmisor(), voided.getId(), OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                SubOperacionLogEnum.GENERATE_TEMPLATE, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

        fileXMLZipBase64 = templateGenerated.get(ConstantesParameter.PARAM_FILE_ZIP_BASE64);
        nameDocument = templateGenerated.get(ConstantesParameter.PARAM_NAME_DOCUMENT);
        nameDocumentComplete = nameDocument + "." + ConstantesParameter.TYPE_FILE_ZIP;

        responseSunat = sendSunat.sendSummary(nameDocumentComplete, fileXMLZipBase64,companyEntity.getRuc());
        Logger.register(TipoLogEnum.INFO, voided.getRucEmisor(), voided.getId(), OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                SubOperacionLogEnum.SEND_SUNAT, responseSunat.toString());

        messageBuilder.append("[").append(voided.getRucEmisor()).append("]");
        messageBuilder.append("[").append(voided.getFechaBaja()).append("]");
        messageBuilder.append("[").append(voided.getId()).append("]");

        resp = new HashMap<>();
        resp.put(ConstantesParameter.PARAM_ESTADO, responseSunat.getEstadoComunicacionSunat().getEstado());

        switch (responseSunat.getEstadoComunicacionSunat()) {
            case SUCCESS_WITH_ERROR_CONTENT:
                resp.put(ConstantesParameter.PARAM_RESPONSE_CODE, responseSunat.getStatusCode());
                messageBuilder.append(responseSunat.getMessage());
                resp.put(ConstantesParameter.PARAM_DESCRIPTION, messageBuilder.toString());
                break;
            case WITHOUT_CONNECTION:
                resp.put(ConstantesParameter.PARAM_RESPONSE_CODE, ComunicacionSunatEnum.WITHOUT_CONNECTION.name());
                messageBuilder.append(responseSunat.getMessage());
                resp.put(ConstantesParameter.PARAM_DESCRIPTION, messageBuilder.toString());
                break;
            default:
        }
        if (!responseSunat.isSuccess()) {
            return resp;
        }

        RegisterFileUploadEntity file = amazonS3ClientService.uploadFileStorage(UtilArchivo.b64ToByteArrayInputStream(fileXMLZipBase64), nameDocument, "voided", companyEntity);

        Logger.register(TipoLogEnum.INFO, voided.getRucEmisor(), voided.getId(), OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                SubOperacionLogEnum.STORAGE_FILE, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);


        voided.setEstadoComprobante(EstadoComprobanteEnum.PROCESO_ENVIO.getCodigo());
        VoidedDocumentsEntity voidedDocumentsEntity = registrarVoidedDocuments(voided, file.getIdRegisterFileSend(), userName, responseSunat.getTicket());
        resp.put(ConstantesParameter.PARAM_NUM_TICKET, voidedDocumentsEntity.getTicketSunat());
        resp.put(ConstantesParameter.PARAM_DESCRIPTION, "Se registro correctamente el documento: " + voided.getId());

        return resp;

    }

    private CompanyEntity completarDatosVoided(Voided voided) {

        Date fechaActual = Calendar.getInstance().getTime();
        Integer correlativo;

        voided.setFechaGeneracion(UtilFormat.fecha(fechaActual, "yyyy-MM-dd"));
        correlativo = documentsVoidedRepository.getCorrelativoGeneracionByDiaInVoidedDocuments(
                voided.getRucEmisor(), voided.getFechaGeneracion());
        correlativo++;
        voided.setCorrelativoGeneracionDia(correlativo);
        CompanyEntity company = companyRepository.findByRuc(voided.getRucEmisor());
        voided.setDenominacionEmisor(company.getRazonSocial());
        voided.setTipoDocumentoEmisor(ConstantesSunat.TIPO_DOCUMENTO_IDENTIDAD_RUC);
        voided.setId(ConstantesSunat.COMUNICACION_BAJA + "-" +
                voided.getFechaGeneracion().replace("-", "") +
                "-" + voided.getCorrelativoGeneracionDia());

        return company;
    }

    @Override
    public Boolean processVoidedTicket(String ticket, String useName, String rucEmisor) {

        VoidedDocumentsEntity voidedDocumentsEntity = documentsVoidedRepository.getVoidedByTicket(ticket);
        Logger.register(TipoLogEnum.INFO, "-", ticket, OperacionLogEnum.STATUS_SUNAT_VOIDED,
                SubOperacionLogEnum.SELECT_BD, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "[" + voidedDocumentsEntity.toString() + "]");

        rucEmisor = voidedDocumentsEntity.getRucEmisor();
        if (voidedDocumentsEntity.getEstado() != null && (voidedDocumentsEntity.getEstado().equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK) || voidedDocumentsEntity.getEstado().equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_ERROR))) {
            return true;
        }

        ResponsePSE responsePSE = statusService.getStatus(ticket, ConstantesSunat.COMUNICACION_BAJA, useName, rucEmisor);
        Logger.register(TipoLogEnum.INFO, rucEmisor, ticket, OperacionLogEnum.STATUS_SUNAT_VOIDED,
                SubOperacionLogEnum.COMPLETED, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "[" + responsePSE.toString() + "]");

        if (responsePSE.getRespuesta() != null && responsePSE.getRespuesta().toString().equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO))
            return false;
        else return true;
    }

    @Transactional
    @Override
    public ResponsePSE anularDocuments(List<VoucherAnnular> documents, String rucEmisor, String userName,
                                       List<String> ticketsVoidedProcess) {

        ResponsePSE respuesta = new ResponsePSE();
        Map<String, List<VoucherAnnular>> documentosBajaByFechaEmisionFacturasMap = new HashMap<>();
        Map<String, List<VoucherAnnular>> documentosBajaByFechaEmisionGuiasMap = new HashMap<>();
        List<VoucherAnnular> documentosVoidedByFechaEmision;
        List<VoucherAnnular> documentosSummary = new ArrayList<>();
        StringBuilder messageBuilder = null;
        List<VoucherAnnular> documentsanular = new ArrayList<>();
        try {
            System.out.println("ENIO ANULAR "+rucEmisor);

            for (VoucherAnnular documento : documents) {
                String identificadorDocumento = rucEmisor + "-" + documento.getTipoComprobante() + "-" +
                        documento.getSerie().toUpperCase() + "-" + documento.getNumero();
                System.out.println(identificadorDocumento);
                PaymentVoucherEntity entity = paymentVoucherRepository.getIdentificadorDocument(identificadorDocumento);
                System.out.println(entity);
                if (documento.getRucEmisor()==null){
                    documento.setRucEmisor(rucEmisor);
                }
                if(entity==null){
                    System.out.println("Document not found");
                    //throw new Exception("Documento no encontrado en los registros");
                }else if (!entity.getEstado().equals("08") ){
                    documentsanular.add(documento);
                   /// INGRESAR AQUI ELIMINAR YA ANULADO LEYTER
                }
            }

            anularValidate.validateVoucherAnnular(documentsanular, rucEmisor);

            Logger.register(TipoLogEnum.INFO, rucEmisor, "-", OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                    SubOperacionLogEnum.VALIDATE_FIELDS, "{" + ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "}"
                            + "{" + documents + "}");

            for (VoucherAnnular document : documents) {
                String identificadorDocumento = rucEmisor + "-" + document.getTipoComprobante() + "-" +
                        document.getSerie().toUpperCase() + "-" + document.getNumero();
                System.out.println(identificadorDocumento);
                PaymentVoucherEntity entity = paymentVoucherRepository.getIdentificadorDocument(identificadorDocumento);
                if (entity==null){
                    if (messageBuilder == null) {
                        messageBuilder = new StringBuilder();
                    }
                    messageBuilder.append("500");
                    messageBuilder.append("No existe documento de referencia");
                }else{
                    switch (document.getTipoComprobante()) {
                        case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                            if (documentosBajaByFechaEmisionFacturasMap.get(document.getFechaEmision()) != null) {
                                documentosVoidedByFechaEmision = documentosBajaByFechaEmisionFacturasMap
                                        .get(document.getFechaEmision());
                            } else {
                                documentosVoidedByFechaEmision = new ArrayList<>();
                            }
                            documentosVoidedByFechaEmision.add(document);
                            documentosBajaByFechaEmisionFacturasMap.put(document.getFechaEmision(), documentosVoidedByFechaEmision);
                            break;
                        case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
                            documentosSummary.add(document);
                            break;
                        case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
                        case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:
                            if (document.getTipoComprobanteRelacionado().equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
                                if (documentosBajaByFechaEmisionFacturasMap.get(document.getFechaEmision()) != null) {
                                    documentosVoidedByFechaEmision = documentosBajaByFechaEmisionFacturasMap
                                            .get(document.getFechaEmision());
                                } else {
                                    documentosVoidedByFechaEmision = new ArrayList<>();
                                }
                                documentosVoidedByFechaEmision.add(document);
                                documentosBajaByFechaEmisionFacturasMap.put(
                                        document.getFechaEmision(),
                                        documentosVoidedByFechaEmision);
                            } else {
                                documentosSummary.add(document);
                            };
                            break;
                        case ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION:
                            if (documentosBajaByFechaEmisionGuiasMap.get(document.getFechaEmision()) != null) {
                                documentosVoidedByFechaEmision = documentosBajaByFechaEmisionGuiasMap.get(document.getFechaEmision());
                            } else {
                                documentosVoidedByFechaEmision = new ArrayList<>();
                            }
                            documentosVoidedByFechaEmision.add(document);
                            documentosBajaByFechaEmisionGuiasMap.put(document.getFechaEmision(), documentosVoidedByFechaEmision);
                            break;
                    }
                }

            }

            for (VoucherAnnular document : documentosSummary) {

                document.setRucEmisor(rucEmisor);
                annularDocumentSendFromSummaryDocuments(document, userName);

            }
            for (String fechaEmision : documentosBajaByFechaEmisionFacturasMap.keySet()) {

                Voided voided = new Voided();
                List<VoidedLine> lines = new ArrayList<>();
                voided.setFechaBaja(fechaEmision);
                voided.setRucEmisor(rucEmisor);

                List<VoucherAnnular> anulados = documentosBajaByFechaEmisionFacturasMap.get(fechaEmision);
                for (VoucherAnnular document : anulados) {
                    String identificadorDocumento = rucEmisor + "-" + document.getTipoComprobante() + "-" +
                            document.getSerie().toUpperCase() + "-" + document.getNumero();
                    PaymentVoucherEntity entity = paymentVoucherRepository.getIdentificadorDocument(identificadorDocumento);
                    if (entity.getEstado().equals("08") ){
                        if (messageBuilder == null) {
                            messageBuilder = new StringBuilder();
                        }
                        messageBuilder.append("[").append("200").append("]");
                        messageBuilder.append("[").append("El comprobante ya ha sido anulado").append("]");
                    }else{
                        VoidedLine item = new VoidedLine();
                        item.setTipoComprobante(document.getTipoComprobante());
                        item.setSerieDocumento(document.getSerie());
                        item.setNumeroDocumento(document.getNumero());
                        item.setRazon(document.getMotivoAnulacion());
                        lines.add(item);
                    }


                }
                voided.setLines(lines);

                Map<String, String> resp = annularDocumentSendVoidedDocuments(voided, userName);
                if (!ComunicacionSunatEnum.getEnum(resp.get(ConstantesParameter.PARAM_ESTADO)).equals(ComunicacionSunatEnum.SUCCESS)) {
                    if (messageBuilder == null) {
                        messageBuilder = new StringBuilder();
                    }
                    messageBuilder.append("[").append(resp.get(ConstantesParameter.PARAM_RESPONSE_CODE)).append("]");
                    messageBuilder.append("[").append(resp.get(ConstantesParameter.PARAM_DESCRIPTION)).append("]");
                } else {
                    ticketsVoidedProcess.add(resp.get(ConstantesParameter.PARAM_NUM_TICKET));
                }
            }
            for (String fechaEmision : documentosBajaByFechaEmisionGuiasMap.keySet()) {

                Voided voided = new Voided();
                List<VoidedLine> lines = new ArrayList<>();
                voided.setFechaBaja(fechaEmision);
                voided.setRucEmisor(rucEmisor);

                List<VoucherAnnular> anulados = documentosBajaByFechaEmisionGuiasMap.get(fechaEmision);
                for (VoucherAnnular document : anulados) {

                    VoidedLine item = new VoidedLine();
                    item.setTipoComprobante(document.getTipoComprobante());
                    item.setSerieDocumento(document.getSerie());
                    item.setNumeroDocumento(document.getNumero());
                    item.setRazon(document.getMotivoAnulacion());
                    lines.add(item);
                }
                voided.setLines(lines);
                Map<String, String> resp = annularDocumentSendVoidedDocuments(voided, userName);
                if (!ComunicacionSunatEnum.getEnum(resp.get(ConstantesParameter.PARAM_ESTADO)).equals(ComunicacionSunatEnum.SUCCESS)) {
                    if (messageBuilder == null) {
                        messageBuilder = new StringBuilder();
                    }
                    messageBuilder.append("[").append(resp.get(ConstantesParameter.PARAM_RESPONSE_CODE)).append("]");
                    messageBuilder.append("[").append(resp.get(ConstantesParameter.PARAM_DESCRIPTION)).append("]");
                } else {
                    ticketsVoidedProcess.add(resp.get(ConstantesParameter.PARAM_NUM_TICKET));
                }
            }
            respuesta.setEstado(true);
            if (messageBuilder != null) {
                respuesta.setMensaje(messageBuilder.toString());
            } else {
                respuesta.setMensaje(ConstantesParameter.MSG_RESP_OK);
            }

        } catch (ValidatorFieldsException e) {

            respuesta.setEstado(false);
            respuesta.setMensaje(e.getMensajeValidacion());

            Logger.register(TipoLogEnum.WARNING, rucEmisor, "-", OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                    SubOperacionLogEnum.VALIDATE_FIELDS, e.getMensajeValidacion());

        } catch (Exception e) {

            respuesta.setEstado(false);
            respuesta.setMensaje(e.getMessage());

            Logger.register(TipoLogEnum.ERROR, rucEmisor, "-", OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                    SubOperacionLogEnum.IN_PROCESS, e.getMessage(), documents.toString(), e);

        }

        Logger.register(TipoLogEnum.INFO, rucEmisor, "-", OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                SubOperacionLogEnum.COMPLETED, respuesta.toString());

        return respuesta;
    }

    /**
     * Metodo para anular boletas y notas asociadas a boletas que ya han sido
     * comunicadas a la sunat.
     */
    public void annularDocumentSendFromSummaryDocuments(VoucherAnnular voucherInput, String userName) {
        System.out.println("Entro aqui para anular boleta");
        Timestamp fechaModificacion;
        String identificador;

        fechaModificacion = new Timestamp(Calendar.getInstance().getTime().getTime());
        identificador = voucherInput.getRucEmisor() + "-" + voucherInput.getTipoComprobante() + "-"
                + voucherInput.getSerie().toUpperCase() + "-" + voucherInput.getNumero();

        PaymentVoucherEntity boletaOrNoteBoleta = paymentVoucherRepository.findByIdentificadorDocumento(identificador);
        //SI QUIERO ANULAR UN COMPROBANTE ANTES DE QUE ESTE ACEPTADO EN SUNAT, SETEO UN FLAG
        if (!boletaOrNoteBoleta.getEstadoSunat().equals(EstadoSunatEnum.ACEPTADO.getAbreviado())) {
            boletaOrNoteBoleta.setBoletaAnuladaSinEmitir(true);
        }

        boletaOrNoteBoleta.setEstado(EstadoComprobanteEnum.PENDIENTE_ANULACION.getCodigo());
        boletaOrNoteBoleta.setEstadoSunat(EstadoSunatEnum.NO_ENVIADO.getAbreviado());
        boletaOrNoteBoleta.setMotivoAnulacion(voucherInput.getMotivoAnulacion());
        boletaOrNoteBoleta.setUserNameModify(userName);
        boletaOrNoteBoleta.setFechaModificacion(fechaModificacion);
        boletaOrNoteBoleta.setEstadoItem(ConstantesParameter.STATE_ITEM_PENDIENTE_ANULACION);

        paymentVoucherRepository.save(boletaOrNoteBoleta);

		/*paymentVoucherRepository.updateAnulacionBoletasAndNotasAsociadas(
									identificador,
									EstadoComprobanteEnum.PENDIENTE_ANULACION.getCodigo(),
									EstadoSunatEnum.NO_ENVIADO.getAbreviado(),
									voucherInput.getMotivoAnulacion(),
									userName,
									fechaModificacion);*/

        Logger.register(TipoLogEnum.INFO, voucherInput.getRucEmisor(), identificador, OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                SubOperacionLogEnum.UPDATE_BD_PAYMENT_VOUCHER, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + ".[" + voucherInput.toString() + "]["
                        + "EstadoComprobante:" + EstadoComprobanteEnum.PENDIENTE_ANULACION.getCodigo() + ","
                        + "EstadoSunat:" + EstadoSunatEnum.NO_ENVIADO.getAbreviado() + ","
                        + "MotivoAnulacion:" + voucherInput.getMotivoAnulacion() + ","
                        + "fechaModificacion:" + fechaModificacion + "]");

    }

    @Override
    public ResponsePSE voidedTicket(String ticket, String useName, String rucEmisor) {

        ResponsePSE responsePSE = new ResponsePSE();

        VoidedDocumentsEntity voidedDocumentsEntity = documentsVoidedRepository.getVoidedByTicket(ticket);

        Logger.register(TipoLogEnum.INFO, "-", ticket, OperacionLogEnum.STATUS_SUNAT_VOIDED,
                SubOperacionLogEnum.SELECT_BD, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "[" + voidedDocumentsEntity.toString() + "]");

        rucEmisor = voidedDocumentsEntity.getRucEmisor();

        responsePSE = statusService.getStatus(ticket, ConstantesSunat.COMUNICACION_BAJA, useName, rucEmisor);


        Logger.register(TipoLogEnum.INFO, rucEmisor, ticket, OperacionLogEnum.STATUS_SUNAT_VOIDED,
                SubOperacionLogEnum.COMPLETED, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK + "[" + responsePSE.toString() + "]");


        return responsePSE;
    }

}
