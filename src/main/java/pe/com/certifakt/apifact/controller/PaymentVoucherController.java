package pe.com.certifakt.apifact.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.bean.*;
import pe.com.certifakt.apifact.dto.*;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.jms.MessageProducer;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.PaymentPaypalEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.PaymentVoucherFileRepository;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.*;
import pe.com.certifakt.apifact.util.ConstantesParameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api")
public class PaymentVoucherController {

    private final PaymentVoucherService paymentVoucherService;
    private final PaymentPaypalService paymentPaypalService;
    private final ComunicationSunatService comunicationSunatService;
    private final MessageProducer messageProducer;

    private final ComprobantesService comprobantesService;

    @Autowired
    private PaymentVoucherFileRepository paymentVoucherFileRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    private final DocumentsSummaryService documentsSummaryService;

    @ResponseBody
    @RequestMapping(value = {"/comprobantes-pago"}, method = RequestMethod.POST)
    public ResponsePSE registrarPaymentVoucher(
            @RequestBody PaymentVoucher voucher,
            HttpServletRequest request,
            @CurrentUser UserPrincipal user) throws ServiceException {


        Map<String, Object> result;
        ResponsePSE resp;

        System.out.println("voucher: "+voucher);
        try {
            voucher.setRucEmisor(user.getRuc());

            result = paymentVoucherService.generationPaymentVoucher(
                    voucher,
                    false,
                    user
            );
            resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
            if (result.get(ConstantesParameter.PARAM_BEAN_SEND_BILL) != null) {
                SendBillDTO dataSendBill = (SendBillDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_BILL);
                if (dataSendBill.getEnvioAutomaticoSunat()) {
                    messageProducer.produceSendBill(dataSendBill);
                }
            }else if(result.get(ConstantesParameter.PARAM_BEAN_SEND_BOLETA)!=null){
                SendBoletaDTO sendBoletaDTO = (SendBoletaDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_BOLETA);
                if (sendBoletaDTO.getEnvioDirecto()){
                    ResponsePSE responsePSE;

                    try {
                        responsePSE = documentsSummaryService.generarSummaryByFechaEmisionAndRuc(
                                sendBoletaDTO.getRuc(),
                                sendBoletaDTO.getFechaEmision(),
                                sendBoletaDTO.getNameDocument(),
                                sendBoletaDTO.getUser()
                        );

                        if (responsePSE.getEstado()) {
                            messageProducer.produceProcessSummary(responsePSE.getTicket(), sendBoletaDTO.getRuc());
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            }

            return resp;
        } catch (Exception e) {
            return ResponsePSE.builder().estado(false).mensaje(e.getMessage()).build();
        }
    }
    @ResponseBody
    @RequestMapping(value = {"/paypal-payments"}, method = RequestMethod.POST)
    public PaymentPaypalDto registrarPaymentPaypal(
            @RequestBody PaymentPaypalDto paypal,
            HttpServletRequest request,
            @CurrentUser UserPrincipal user) throws ServiceException {

        PaymentPaypalEntity result = null;
        PaymentPaypalDto dto = null;
        ResponsePSE resp;

        try {
            //voucher.setRucEmisor(user.getRuc());


            return dto;
        } catch (Exception e) {
            return PaymentPaypalDto.builder().estado(false).mensaje(e.getMessage()).build();
        }
    }

    @RequestMapping(value = {"/get-payments-by-day"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByDay(
            @RequestParam(name = "diff", required = true) Integer diff,
            @CurrentUser UserPrincipal userPrincipal) {
        return new ResponseEntity<Object>(comprobantesService.getPaymentsByDay(userPrincipal.getRuc(),diff), HttpStatus.OK);
    }

    @RequestMapping(value = {"/get-payments-by-month"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByMonth(
            @CurrentUser UserPrincipal userPrincipal) {
        return new ResponseEntity<Object>(comprobantesService.getPaymentsByMonth(userPrincipal.getRuc()), HttpStatus.OK);
    }

    @RequestMapping(value = {"/get-payments-by-type"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByType(
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getPaymentsByType(userPrincipal.getRuc()), HttpStatus.OK);
    }

    @RequestMapping(value = {"/get-payments-by-type-month"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByTypeMonth(
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getPaymentsByTypeMonth(userPrincipal.getRuc()), HttpStatus.OK);
    }

    @RequestMapping(value = {"/get-payments-by-type-state"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByTypeAndState(
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getPaymentsByTypeAndState(userPrincipal.getRuc()), HttpStatus.OK);
    }

    @RequestMapping(value = {"/get-payments-by-type-state-month"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByTypeAndStateMonth(
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getPaymentsByTypeAndStateMonth(userPrincipal.getRuc()), HttpStatus.OK);
    }

    @RequestMapping(value = {"/get-payments-by-date-and-type"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByDateAndType(
            @RequestParam(name = "diff", required = true) Integer diff,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getPaymentsColumnLine(userPrincipal.getRuc(),diff), HttpStatus.OK);
    }
    @RequestMapping(value = {"/get-payments-by-date-and-type-month"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByDateAndTypeMonth(
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getPaymentsColumnLineMonths(userPrincipal.getRuc()), HttpStatus.OK);
    }

    @RequestMapping(value = {"/get-payments-by-user-and-day"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByUserAndDay(
            @RequestParam(name = "diff", required = true) Integer diff,
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getPaymentsByUserAndDay(userPrincipal.getRuc(),diff), HttpStatus.OK);
    }

    @RequestMapping(value = {"/get-payments-by-user-and-month"}, method = RequestMethod.GET)
    public ResponseEntity<?> getPaymentsByUserAndMonth(
            @CurrentUser UserPrincipal userPrincipal) {

        return new ResponseEntity<Object>(comprobantesService.getPaymentsByUserAndMonth(userPrincipal.getRuc()), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = {"/regenerar-comprobante-by-idsummary/{idSummary}"}, method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Map<String,Object>> resendPaymentVoucherBySummary(
            @PathVariable Long idSummary,
            HttpServletRequest request,
            @CurrentUser UserPrincipal user) throws ServiceException {

        List<Map<String, Object>> result;
        ResponsePSE resp;
        result = paymentVoucherService.resendPaymentVoucherBySummary(
                idSummary,
                user
        );
        //resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

        return result;
    }
    @ResponseBody
    @RequestMapping(value = {"/regenerar-comprobante/{idPayment}"}, method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponsePSE resendPaymentVoucher(
            @PathVariable Long idPayment,
            HttpServletRequest request,
            @CurrentUser UserPrincipal user) throws ServiceException {
        Map<String, Object> result;
        ResponsePSE resp;
        result = paymentVoucherService.resendPaymentVoucher(
                idPayment,
                user
        );
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
        return resp;
    }

    @ResponseBody
    @RequestMapping(value = {"/regenerar-comprobante-Onlyxml/{idPayment}"}, method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponsePSE resendPaymentVoucherOnlyxml(
            @PathVariable Long idPayment,
            HttpServletRequest request,
            @CurrentUser UserPrincipal user) throws ServiceException {
        System.out.println("Ingresando send sunat 7 dias");
        Map<String, Object> result;
        ResponsePSE resp=null;
        //paymentVoucherService.getLisVoucherSeven();
        //Map<String,String> stringMap = paymentVoucherService.postVoucherSeven();
        //System.out.println(stringMap);

        result = paymentVoucherService.resendPaymentVoucherOnlyxml(
                idPayment,
                user
        );
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
        return resp;
    }

    @ResponseBody
    @RequestMapping(value = {"/editar-comprobante"}, method = RequestMethod.POST)
    public ResponsePSE editarPaymentVoucher(
            @RequestBody PaymentVoucher voucher,
            HttpServletRequest request,
            @CurrentUser UserPrincipal user) throws ServiceException {

        Map<String, Object> result;
        ResponsePSE resp;

        voucher.setRucEmisor(user.getRuc());
        result = paymentVoucherService.generationPaymentVoucher(
                voucher,
                true,
                user
        );
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_BILL) != null) {
            SendBillDTO dataSendBill = (SendBillDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_BILL);
            if (dataSendBill.getEnvioAutomaticoSunat()) {
                messageProducer.produceSendBill(dataSendBill);
            }
        }

        return resp;
    }


    @ResponseBody
    @RequestMapping(value = {"/comprobantes/enviar-sunat"}, method = RequestMethod.POST)
    public ResponsePSE enviarComprobanteASunat(
            @RequestBody IdentificadorComprobante comprobante,
            @CurrentUser UserPrincipal user,
            HttpServletRequest request) throws ServiceException {

        System.out.println("--------------ESTAA ENVIANDO EL COMPROBANTE A LA SUNAT");

        Map<String, Object> result;
        ResponsePSE resp;

        PaymentVoucherEntity paymentVoucherEntity = paymentVoucherService.prepareComprobanteForEnvioSunat(user.getRuc(),
                comprobante.getTipo(), comprobante.getSerie(), comprobante.getNumero());

        result = comunicationSunatService.sendDocumentBill(
                paymentVoucherEntity.getRucEmisor(),
                paymentVoucherEntity.getIdPaymentVoucher()
        );
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

        if (resp.getEstado()) {
            messageProducer.produceEnviarCorreo(EmailSendDTO.builder().id(paymentVoucherEntity.getIdPaymentVoucher()).build());
        }


        if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {

            GetStatusCdrDTO dataGetStatusCDR = (GetStatusCdrDTO) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
            messageProducer.produceGetStatusCDR(dataGetStatusCDR);

        }


        return resp;
    }



    @ResponseBody
    @RequestMapping(value = {"/comprobantes/7dias"}, method = RequestMethod.GET)
    public  List<String> verComprobanteSunat7dias(
            @CurrentUser UserPrincipal user) throws ServiceException {


        List<PaymentVoucherInterDto> facturasPorEnviar = paymentVoucherService.getFacturasNoEnviadasCon7Dias();
        log.info("Facturas no enviadas con 7 dias: "+facturasPorEnviar.size());

        List<String> results = new ArrayList<>();


        for (PaymentVoucherInterDto p : facturasPorEnviar){

            results.add(String.format("%s, FE: %s, EL: %s ES: %s", p.getIdentificador(), p.getFechaEmision(), p.getEstado(), p.getEstadoSunat()));
        }

        return results;
    }



    @ResponseBody
    @RequestMapping(value = {"/comprobantes/enviar-sunat-7dias"}, method = RequestMethod.POST)
    public  List<ResponsePSE> enviarComprobanteASunat7ias(
            @CurrentUser UserPrincipal user) throws ServiceException {


        List<PaymentVoucherInterDto> facturasPorEnviar = paymentVoucherService.getFacturasNoEnviadasCon7Dias();
        log.info("Facturas no enviadas con 7 dias: "+facturasPorEnviar.size());

        List<ResponsePSE> results = new ArrayList<>();


        for (PaymentVoucherInterDto paymentVoucherEntity : facturasPorEnviar){

            try {
                ResponsePSE resp;
                Map<String, Object> result = comunicationSunatService.sendDocumentBill(
                        paymentVoucherEntity.getEmisor(),
                        paymentVoucherEntity.getId()
                );
                resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
                if (resp.getEstado()) {
                    messageProducer.produceEnviarCorreo(EmailSendDTO.builder().id(paymentVoucherEntity.getId()).build());
                }
                results.add(resp);
            } catch (Exception e){
                log.error("Error envio facturas con 7 dias",e);
            }
        }

        return results;
    }






    @ResponseBody
    @PostMapping("/consultar-cdr")
    public ResponsePSE obtenerCDRPaymentVoucher(
            @RequestBody GetStatusCdrDTO getStatusCdr,
            HttpServletRequest request,
            @CurrentUser UserPrincipal user) throws ServiceException {

        String authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
        return paymentVoucherService.consultaCdrComprobante(authorization, user.getRuc(), getStatusCdr.getTipoComprobante(), getStatusCdr.getSerie(), getStatusCdr.getNumero());
    }


    @RequestMapping(value = {"/lista-comprobantes"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponsePSE paymentVoucherList(@RequestBody PaymentVoucherParamsInput params,
                                          HttpServletRequest request,
                                          HttpServletResponse response,
                                          @CurrentUser UserPrincipal user) {

        params.setRucEmisor(user.getRuc());
        ResponsePSE resp = paymentVoucherService.getDocuments(params);

        return resp;
    }

    @RequestMapping(value = {"/lista-comprobantes-idDocumentos"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponsePSE paymentVoucherListByIdentificadorDocumento(@RequestBody List<Comprobante> datosComprobantes,
                                                                  HttpServletRequest request,
                                                                  HttpServletResponse response,
                                                                  @CurrentUser UserPrincipal user) {
        ResponsePSE resp = paymentVoucherService.getDocumentsByIdentificadores(datosComprobantes, user.getRuc());

        return resp;
    }


    @ResponseBody
    @RequestMapping(value = {"/enviar-comprobantes-empresa"}, method = RequestMethod.POST)
    public List<ResponsePSE> enviarComprobantesByEmpresa(
            @RequestBody(required = false) IdentificadorComprobante comprobante,
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser UserPrincipal user) {

        Map<String, Object> result;
        String authorization;
        ResponsePSE resp = null;
        List<ResponsePSE> listResponsePSE = new ArrayList<ResponsePSE>();


        try {

            CompanyEntity company = companyRepository.findByRuc(user.getRuc());
            boolean existeEnvioAutomatico = (company.getEnvioAutomaticoSunat() != null ? company.getEnvioAutomaticoSunat() : false) && (company.getEstado().equals(ConstantesParameter.REGISTRO_ACTIVO));

            if (existeEnvioAutomatico) {

                List<PaymentVoucherEntity> listPaymentVoucher = paymentVoucherRepository.getListPaymentVoucherPorEnviarSunat(user.getRuc());

                for (PaymentVoucherEntity voucher : listPaymentVoucher) {

                    if (paymentVoucherFileRepository.getPaymentVoucherFileEntityCDR(voucher.getIdPaymentVoucher()) == null) {

                        resp = new ResponsePSE();

                        PaymentVoucherEntity paymentVoucherEntity = paymentVoucherService.prepareComprobanteForEnvioSunat(user.getRuc(), voucher.getTipoComprobante(), voucher.getSerie(), voucher.getNumero());

                        authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
                        result = comunicationSunatService.sendDocumentBill(paymentVoucherEntity.getRucEmisor(), paymentVoucherEntity.getIdPaymentVoucher());

                        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
                        listResponsePSE.add(resp);

                        if (resp.getEstado()) {
                            messageProducer.produceEnviarCorreo(EmailSendDTO.builder().id(paymentVoucherEntity.getIdPaymentVoucher()).build());
                        }

                        if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {

                            GetStatusCdrDTO dataGetStatusCDR = (GetStatusCdrDTO) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
                            messageProducer.produceGetStatusCDR(dataGetStatusCDR);
                        }
                    }
                }
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


    /*@ResponseBody
    @RequestMapping(value = {"/enviar-comprobantes"}, method = RequestMethod.POST)
    public List<ResponsePSE> enviaComprobantes(
            @RequestBody(required = false) IdentificadorComprobante comprobante,
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser UserPrincipal user
    ) {

        Map<String, Object> result;
        String authorization;
        ResponsePSE resp = null;
        List<ResponsePSE> listResponsePSE = new ArrayList<ResponsePSE>();

        try {

            List<PaymentVoucherEntity> listPaymentVoucherErrores = paymentVoucherRepository.getListPaymentVoucherErrores();

            for (PaymentVoucherEntity voucher : listPaymentVoucherErrores) {

                paymentVoucherService.actualizarEstadoComprobante(voucher);

            }

            List<String> listRucs = companyRepository.getCompaniesForSummaryDocuments();

            for (String ruc : listRucs) {

                CompanyEntity company = companyRepository.findByRuc(ruc);
                boolean existeEnvioAutomatico = (company.getEnvioAutomaticoSunat() != null ? company.getEnvioAutomaticoSunat() : false) && (company.getEstado().equals(ConstantesParameter.REGISTRO_ACTIVO));

                if (existeEnvioAutomatico) {


                    List<PaymentVoucherEntity> listPaymentVoucher = paymentVoucherRepository.getListPaymentVoucherPorEnviarSunat(ruc);

                    for (PaymentVoucherEntity voucher : listPaymentVoucher) {

                        if (paymentVoucherFileRepository.getPaymentVoucherFileEntityCDR(voucher.getIdPaymentVoucher()) == null) {

                            resp = new ResponsePSE();

                            PaymentVoucherEntity paymentVoucherEntity = paymentVoucherService.prepareComprobanteForEnvioSunat(ruc, voucher.getTipoComprobante(), voucher.getSerie(), voucher.getNumero());

                            authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
                            result = comunicationSunatService.sendDocumentBill(paymentVoucherEntity.getRucEmisor(), paymentVoucherEntity.getIdPaymentVoucher());

                            resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
                            listResponsePSE.add(resp);

                            if (resp.getEstado()) {
                                messageProducer.produceEnviarCorreo(EmailSendDTO.builder().id(paymentVoucherEntity.getIdPaymentVoucher()).build());
                            }

                            if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {

                                GetStatusCdrDTO dataGetStatusCDR = (GetStatusCdrDTO) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
                                messageProducer.produceGetStatusCDR(dataGetStatusCDR);
                            }
                        }
                    }
                }
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

    }*/
}
