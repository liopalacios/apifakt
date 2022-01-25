package pe.com.certifakt.apifact.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.bean.IdentificadorComprobante;
import pe.com.certifakt.apifact.bean.ResponsePSE;
import pe.com.certifakt.apifact.bean.SummaryResponse;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.jms.MessageProducer;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.model.SummaryDocumentEntity;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.repository.SummaryDocumentRepository;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.DocumentsSummaryService;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.validate.SummaryValidate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api")
public class SummaryController {

    private final DocumentsSummaryService documentsSummaryService;
    private final MessageProducer messageProducer;
    private final SummaryValidate validate;
    private final SummaryDocumentRepository summaryDocumentRepository;

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    @PostMapping("/resumen-diario/{fechaEmision}")
    public ResponseEntity<?> summaryByFechaEmision(
            @PathVariable("fechaEmision") String fechaEmision,
            @RequestBody(required = false) IdentificadorComprobante comprobante,
            @CurrentUser UserPrincipal user) {

        ResponsePSE responsePSE;

        try {
            validate.validateSummaryByFechaEmision(user.getRuc(), fechaEmision);
            responsePSE = documentsSummaryService.generarSummaryByFechaEmisionAndRuc(
                    user.getRuc(),
                    fechaEmision,
                    comprobante,
                    user.getUsername()
            );

            if (responsePSE.getEstado()) {
                messageProducer.produceProcessSummary(responsePSE.getTicket(), user.getRuc());
            }
        } catch (ValidatorFieldsException e) {

            responsePSE = new ResponsePSE();
            responsePSE.setEstado(false);
            responsePSE.setMensaje(e.getMensajeValidacion());
        }

        return new ResponseEntity<ResponsePSE>(responsePSE, HttpStatus.OK);

    }

    @PostMapping("/resumen-notas/{fechaEmision}")
    public ResponseEntity<?> summaryNotasByFechaEmision(
            @PathVariable("fechaEmision") String fechaEmision,
            @RequestBody(required = false) IdentificadorComprobante comprobante,
            @CurrentUser UserPrincipal user) {

        ResponsePSE responsePSE;

        try {
            validate.validateSummaryByFechaEmision(user.getRuc(), fechaEmision);
            responsePSE = documentsSummaryService.generarSummaryNotaCreditoByFechaEmisionAndRuc(
                    user.getRuc(),
                    fechaEmision,
                    comprobante,
                    user.getUsername()
            );

            if (responsePSE.getEstado()) {
                messageProducer.produceProcessSummary(responsePSE.getTicket(), user.getRuc());
            }
        } catch (ValidatorFieldsException e) {

            responsePSE = new ResponsePSE();
            responsePSE.setEstado(false);
            responsePSE.setMensaje(e.getMensajeValidacion());
        }

        return new ResponseEntity<ResponsePSE>(responsePSE, HttpStatus.OK);

    }


    @PostMapping("/scheduler/task/summary/{rucEmisor}/{fechaEmision}")
    public ResponseEntity<?> summaryFromScheduler(@PathVariable("rucEmisor") String rucEmisor,
                                                  @PathVariable("fechaEmision") String fechaEmision, HttpServletRequest request,
                                                  HttpServletResponse response) {

        String userName = ConstantesParameter.USER_API_SCHEDULER;
        ResponsePSE resp = null;

        List<PaymentVoucherEntity> listPaymentVoucher = paymentVoucherRepository.getListPaymentVoucherForSummaryDocuments(rucEmisor, fechaEmision);
        List<PaymentVoucherEntity> listComprobantes = listPaymentVoucher.subList(0, listPaymentVoucher.size() > 499 ? 499 : listPaymentVoucher.size());

        while (listComprobantes != null && listComprobantes.size() > 0) {

            resp = documentsSummaryService.generarSummaryByFechaEmisionAndRuc(rucEmisor, fechaEmision, null, userName);

            if (resp.getEstado()) {
                messageProducer.produceProcessSummary(resp.getTicket(), rucEmisor);
            }

            List<PaymentVoucherEntity> listPaymentVoucherEntity = paymentVoucherRepository.getListPaymentVoucherForSummaryDocuments(rucEmisor, fechaEmision);

            if (listPaymentVoucherEntity != null && listPaymentVoucherEntity.size() > 0) {

                listComprobantes = listPaymentVoucherEntity.subList(0, listPaymentVoucherEntity.size() > 499 ? 499 : listPaymentVoucherEntity.size());

            } else {
                listComprobantes = null;
            }
        }

        return new ResponseEntity<ResponsePSE>(resp, HttpStatus.OK);

    }

    //Creado Ahora
    @PostMapping("/scheduler/task/summaryNotasCredito/{rucEmisor}/{fechaEmision}")
    public ResponseEntity<?> NotaCreditosummaryFromScheduler(@PathVariable("rucEmisor") String rucEmisor,
                                                  @PathVariable("fechaEmision") String fechaEmision, HttpServletRequest request,
                                                  HttpServletResponse response) {

        String userName = ConstantesParameter.USER_API_SCHEDULER;
        ResponsePSE resp = null;

        List<PaymentVoucherEntity> listPaymentVoucher = paymentVoucherRepository.getListPaymentVoucherForSummaryDocumentsNotaCredito(rucEmisor, fechaEmision);
        List<PaymentVoucherEntity> listComprobantes = listPaymentVoucher.subList(0, listPaymentVoucher.size() > 499 ? 499 : listPaymentVoucher.size());

        while (listComprobantes != null && listComprobantes.size() > 0) {

            resp = documentsSummaryService.generarSummaryByFechaEmisionAndRuc(rucEmisor, fechaEmision, null, userName);

            if (resp.getEstado()) {
                messageProducer.produceProcessSummary(resp.getTicket(), rucEmisor);
            }

            List<PaymentVoucherEntity> listPaymentVoucherEntity = paymentVoucherRepository.getListPaymentVoucherForSummaryDocumentsNotaCredito(rucEmisor, fechaEmision);

            if (listPaymentVoucherEntity != null && listPaymentVoucherEntity.size() > 0) {

                listComprobantes = listPaymentVoucherEntity.subList(0, listPaymentVoucherEntity.size() > 499 ? 499 : listPaymentVoucherEntity.size());

            } else {
                listComprobantes = null;
            }
        }

        return new ResponseEntity<ResponsePSE>(resp, HttpStatus.OK);

    }


    @GetMapping("/summary/detail/{fechaGeneracion}")
    @ResponseBody
    public List<SummaryResponse> detailSummaryByFechaGeneracion(
            @PathVariable("fechaGeneracion") String fechaGeneracion,
            HttpServletRequest request,
            HttpServletResponse response) {

        List<SummaryResponse> lista = documentsSummaryService.listarSummariesByFechaGeneracion(fechaGeneracion);

        return lista;
    }


    @ResponseBody
    @RequestMapping(value = {"/enviar-documentos-proceso/{fechaEmision}"}, method = RequestMethod.POST)
    public List<ResponsePSE> enviarDocumentosProcesoByFechaEmision(
            @PathVariable("fechaEmision") String fechaEmision,
            HttpServletRequest request,
            HttpServletResponse response) {


        List<SummaryDocumentEntity> listSummaryDocument = summaryDocumentRepository.getSummariesByFechaGeneracionPendientes(fechaEmision);
        List<ResponsePSE> listResponsePSE = new ArrayList<ResponsePSE>();
        ResponsePSE resp = null;
        String authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
        String userName = ConstantesParameter.USER_API_QUEUE;
        String rucEmisor = null;

        try {

            for (SummaryDocumentEntity sumaryDocument : listSummaryDocument) {

                resp = new ResponsePSE();
                resp = documentsSummaryService.processSummaryTicket(sumaryDocument.getTicketSunat(), userName, rucEmisor);
                listResponsePSE.add(resp);

            }

            if (!(listResponsePSE.size() > 0)) {

                ResponsePSE responsePSE = new ResponsePSE();
                responsePSE.setMensaje("NO EXISTEN COMPROBANTES PENDIENTES");
                listResponsePSE.add(responsePSE);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return listResponsePSE;

    }


    @ResponseBody
    @RequestMapping(value = {"/enviar-documentos-proceso"}, method = RequestMethod.POST)
    public List<ResponsePSE> enviarDocumentosProceso(
            HttpServletRequest request,
            HttpServletResponse response) {


        List<SummaryDocumentEntity> listSummaryDocument = summaryDocumentRepository.getSummariesByGeneracionPendientes();
        List<ResponsePSE> listResponsePSE = new ArrayList<ResponsePSE>();
        ResponsePSE resp = null;
        String authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
        String userName = ConstantesParameter.USER_API_QUEUE;
        String rucEmisor = null;

        try {

            for (SummaryDocumentEntity sumaryDocument : listSummaryDocument) {

                resp = new ResponsePSE();
                Thread.sleep(2000);

                resp = documentsSummaryService.processSummaryTicket(sumaryDocument.getTicketSunat(), userName, rucEmisor);
                listResponsePSE.add(resp);

            }
            if (!(listResponsePSE.size() > 0)) {

                ResponsePSE responsePSE = new ResponsePSE();
                responsePSE.setMensaje("NO EXISTEN COMPROBANTES PENDIENTES");
                listResponsePSE.add(responsePSE);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return listResponsePSE;
    }

}
