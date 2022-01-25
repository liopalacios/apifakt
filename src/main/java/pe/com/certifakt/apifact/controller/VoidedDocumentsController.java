package pe.com.certifakt.apifact.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.bean.Comprobante;
import pe.com.certifakt.apifact.bean.IdentificadorComprobante;
import pe.com.certifakt.apifact.bean.ResponsePSE;
import pe.com.certifakt.apifact.bean.VoucherAnnular;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.jms.MessageProducer;
import pe.com.certifakt.apifact.model.VoidedDocumentsEntity;
import pe.com.certifakt.apifact.repository.DocumentsVoidedRepository;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.DocumentsSummaryService;
import pe.com.certifakt.apifact.service.DocumentsVoidedService;
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
public class VoidedDocumentsController {

    private final DocumentsVoidedService documentsVoidedService;
    private final MessageProducer messageProducer;
    private final DocumentsVoidedRepository documentsVoidedRepository;
    private final SummaryValidate validate;
    private final DocumentsSummaryService documentsSummaryService;

    @RequestMapping(value = {"/anulacion-comprobantes"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponsePSE anulacionDocumentos(@RequestBody List<VoucherAnnular> documentosToAnular,
                                           HttpServletRequest request,
                                           HttpServletResponse response,
                                           @CurrentUser UserPrincipal user) {

        List<String> ticketsVoidedProcess = new ArrayList<>();

        ResponsePSE resp = documentsVoidedService.anularDocuments(
                documentosToAnular,
                user.getRuc(),
                user.getUsername(), ticketsVoidedProcess);

        if(documentosToAnular.get(0).getTipoComprobante().contains("03")){
            ResponsePSE responsePSE;
            try {
                validate.validateSummaryByFechaEmision(documentosToAnular.get(0).getRucEmisor(), documentosToAnular.get(0).getFechaEmision());
                IdentificadorComprobante comprobante = new IdentificadorComprobante(documentosToAnular.get(0).getTipoComprobante(),documentosToAnular.get(0).getSerie(),documentosToAnular.get(0).getNumero());
                responsePSE = documentsSummaryService.generarSummaryByFechaEmisionAndRuc(
                        documentosToAnular.get(0).getRucEmisor(),
                        documentosToAnular.get(0).getFechaEmision(),
                        comprobante,
                        user.getUsername()
                );
                System.out.println(responsePSE.toString());
                if (responsePSE.getEstado()) {
                    messageProducer.produceProcessSummary(responsePSE.getTicket(), documentosToAnular.get(0).getRucEmisor());
                }
            } catch (ValidatorFieldsException e) {

                responsePSE = new ResponsePSE();
                responsePSE.setEstado(false);
                responsePSE.setMensaje(e.getMensajeValidacion());
            }
        }

        ticketsVoidedProcess.forEach(s -> messageProducer.produceProcessVoided(s, user.getRuc()));

        return resp;
    }

    @GetMapping("/voided/detail/{numeroTicket}")
    @ResponseBody
    public List<Comprobante> detailAnnulledByTicket(
            @PathVariable("numeroTicket") String numeroTicket,
            HttpServletRequest request,
            HttpServletResponse response) {

        List<Comprobante> lista = documentsVoidedService.listarIdentificadorDocumentoByTicket(numeroTicket);

        return lista;
    }


    @ResponseBody
    @RequestMapping(value = {"/enviar-comprobantes-anular/{fechaBajaDocs}"}, method = RequestMethod.POST)
    public List<ResponsePSE> enviarComprobantesAnularByFechaBajaDoc(
            @PathVariable("fechaBajaDocs") String fechaBajaDocs,
            HttpServletRequest request,
            HttpServletResponse response) {


        List<VoidedDocumentsEntity> listBajaDocumentos = documentsVoidedRepository.getVoidedPendientesByFechaBajaDoc(fechaBajaDocs);
        List<ResponsePSE> listResponsePSE = new ArrayList<ResponsePSE>();
        ResponsePSE resp = null;
        String authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
        String userName = ConstantesParameter.USER_API_QUEUE;
        String rucEmisor = null;


        try {

            for (VoidedDocumentsEntity documentoBaja : listBajaDocumentos) {

                resp = new ResponsePSE();
                Thread.sleep(2000);

                resp = documentsVoidedService.voidedTicket(
                        documentoBaja.getTicketSunat(),
                        userName,
                        rucEmisor
                );

                listResponsePSE.add(resp);

                if (resp.getRespuesta() != null && resp.getRespuesta().toString().equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK)) {
                    log.info("TICKET PROCESADO " + documentoBaja.getTicketSunat());
                    messageProducer.produceEnviarCorreoAnulacion(documentoBaja.getTicketSunat());
                }

            }

            if (!(listResponsePSE.size() > 0)) {

                ResponsePSE responsePSE = new ResponsePSE();
                responsePSE.setMensaje("NO EXISTEN COMPROBANTES PENDIENTES DE BAJA");
                listResponsePSE.add(responsePSE);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }


        return listResponsePSE;
    }


    @ResponseBody
    @RequestMapping(value = {"/enviar-comprobantes-anular"}, method = RequestMethod.POST)
    public List<ResponsePSE> enviarComprobantesByAnular(
            HttpServletRequest request,
            HttpServletResponse response) {


        List<VoidedDocumentsEntity> listBajaDocumentos = documentsVoidedRepository.getVoidedPendientes();
        List<ResponsePSE> listResponsePSE = new ArrayList<ResponsePSE>();
        ResponsePSE resp = null;
        String authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
        String userName = ConstantesParameter.USER_API_QUEUE;
        String rucEmisor = null;


        try {

            for (VoidedDocumentsEntity documentoBaja : listBajaDocumentos) {

                resp = new ResponsePSE();
                Thread.sleep(2000);

                resp = documentsVoidedService.voidedTicket(
                        documentoBaja.getTicketSunat(),
                        userName,
                        rucEmisor
                );

                listResponsePSE.add(resp);

                if (resp.getRespuesta() != null && resp.getRespuesta().toString().equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK)) {
                    log.info("TICKET PROCESADO " + documentoBaja.getTicketSunat());
                    messageProducer.produceEnviarCorreoAnulacion(documentoBaja.getTicketSunat());
                }

            }

            if (!(listResponsePSE.size() > 0)) {

                ResponsePSE responsePSE = new ResponsePSE();
                responsePSE.setMensaje("NO EXISTEN COMPROBANTES PENDIENTES DE BAJA");
                listResponsePSE.add(responsePSE);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }


        return listResponsePSE;
    }

}
