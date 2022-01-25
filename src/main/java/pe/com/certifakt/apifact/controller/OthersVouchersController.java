package pe.com.certifakt.apifact.controller;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.dto.GetStatusCdrDTO;
import pe.com.certifakt.apifact.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.bean.GuiaRemision;
import pe.com.certifakt.apifact.bean.IdentificadorComprobante;
import pe.com.certifakt.apifact.bean.OtherDocumentCpe;
import pe.com.certifakt.apifact.bean.ResponsePSE;
import pe.com.certifakt.apifact.dto.SendOtherDocumentDTO;
import pe.com.certifakt.apifact.jms.MessageProducer;
import pe.com.certifakt.apifact.model.GuiaRemisionEntity;
import pe.com.certifakt.apifact.model.OtherCpeEntity;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.ComunicationSunatService;
import pe.com.certifakt.apifact.service.OthersVouchersService;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class OthersVouchersController {

    private final OthersVouchersService othersVouchersService;
    private final MessageProducer messageProducer;
    private final ComunicationSunatService comunicationSunatService;


    @PostMapping("/guia-remision")
    @ResponseBody
    @Transactional
    public ResponsePSE generateGuiaRemision(@RequestBody GuiaRemision guiaRemision,
                                            HttpServletRequest request,
                                            HttpServletResponse response,
                                            @CurrentUser UserPrincipal user) {

        Map<String, Object> result;
        ResponsePSE resp;
        guiaRemision.setNumeroDocumentoIdentidadRemitente(user.getRuc());
        guiaRemision.setTipoComprobante(ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION);
        result = othersVouchersService.generationGuiaRemision(
                guiaRemision,
                request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION),
                false,
                user.getUsername());
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {
            SendOtherDocumentDTO guiaRemisionData = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
            if (guiaRemisionData.getEnvioAutomaticoSunat()) {
                messageProducer.produceSendGuiaRemision(guiaRemisionData);
            }
        }
        return resp;
    }




    @PostMapping("/editar-guia")
    @ResponseBody
    @Transactional
    public ResponsePSE editarGuiaRemision(@RequestBody GuiaRemision guiaRemision,
                                            HttpServletRequest request,
                                            HttpServletResponse response,
                                            @CurrentUser UserPrincipal user) {

        Map<String, Object> result;
        ResponsePSE resp;
        guiaRemision.setNumeroDocumentoIdentidadRemitente(user.getRuc());
        guiaRemision.setTipoComprobante(ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION);

        result = othersVouchersService.generationGuiaRemision(
                guiaRemision,
                request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION),
                true,
                user.getUsername());
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {
            SendOtherDocumentDTO guiaRemisionData = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
            if (guiaRemisionData.getEnvioAutomaticoSunat()) {
                messageProducer.produceSendGuiaRemision(guiaRemisionData);
            }

        }

        return resp;
    }

    @PostMapping("/editar-retencion")
    @ResponseBody
    @Transactional
    public ResponsePSE editarRetencion(@RequestBody OtherDocumentCpe retention,
                                          HttpServletRequest request,
                                          HttpServletResponse response,
                                          @CurrentUser UserPrincipal user) {

        System.out.println("EDITANDO RETENCION");

        Map<String, Object> result;
        ResponsePSE resp;
        retention.setNumeroDocumentoIdentidadEmisor(user.getRuc());

        System.out.println("Retencion: "+retention);

        result = othersVouchersService.generationOtherDocument(
                retention,
                request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION),
                true,
                user.getUsername());
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {
            SendOtherDocumentDTO dataOtroCpe = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
            if (dataOtroCpe.getEnvioAutomaticoSunat() != null && dataOtroCpe.getEnvioAutomaticoSunat()) {
                messageProducer.produceSendOtrosCpe(dataOtroCpe);
            }

        }

        return resp;
    }

    @PostMapping("/editar-percepcion")
    @ResponseBody
    @Transactional
    public ResponsePSE editarPercepcion(@RequestBody OtherDocumentCpe perception,
                                       HttpServletRequest request,
                                       HttpServletResponse response,
                                       @CurrentUser UserPrincipal user) {

        System.out.println("EDITANDO PERCEPCION");

        Map<String, Object> result;
        ResponsePSE resp;
        perception.setNumeroDocumentoIdentidadEmisor(user.getRuc());
        //    perception.setTipoComprobante(ConstantesSunat.TIPO_DOCUMENTO_PERCEPTION);

        System.out.println("Percepcion: "+perception);

        result = othersVouchersService.generationOtherDocument(
                perception,
                request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION),
                true,
                user.getUsername());
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {
            SendOtherDocumentDTO dataOtroCpe = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
            if (dataOtroCpe.getEnvioAutomaticoSunat() != null && dataOtroCpe.getEnvioAutomaticoSunat()) {
                messageProducer.produceSendOtrosCpe(dataOtroCpe);
            }

        }

        return resp;
    }

    @PostMapping("/retencion")
    @ResponseBody
    public ResponsePSE generateRetention(@RequestBody OtherDocumentCpe retention,
                                         HttpServletRequest request,
                                         HttpServletResponse response,
                                         @CurrentUser UserPrincipal user) {

        Map<String, Object> result;
        ResponsePSE resp;
        retention.setNumeroDocumentoIdentidadEmisor(user.getRuc());
//		retention.setTipoComprobante(ConstantesSunat.TIPO_DOCUMENTO_RETENTION);

        result = othersVouchersService.generationOtherDocument(
                retention,
                request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION),
                false,
                user.getUsername());
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {

            SendOtherDocumentDTO dataOtroCpe = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
            if (dataOtroCpe.getEnvioAutomaticoSunat() != null && dataOtroCpe.getEnvioAutomaticoSunat()) {
                messageProducer.produceSendOtrosCpe(dataOtroCpe);
            }
        }

        return resp;
    }

    @PostMapping("/percepcion")
    @ResponseBody
    public ResponsePSE generatePerception(@RequestBody OtherDocumentCpe perception,
                                          HttpServletRequest request,
                                          HttpServletResponse response,
                                          @CurrentUser UserPrincipal user) {

        Map<String, Object> result;
        ResponsePSE resp;

        perception.setNumeroDocumentoIdentidadEmisor(user.getRuc());
//		perception.setTipoComprobante(ConstantesSunat.TIPO_DOCUMENTO_PERCEPTION);

        result = othersVouchersService.generationOtherDocument(
                perception,
                request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION),
                false,
                user.getUsername());
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);
        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {

            SendOtherDocumentDTO dataOtroCpe = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
            if (dataOtroCpe.getEnvioAutomaticoSunat() != null && dataOtroCpe.getEnvioAutomaticoSunat()) {
                messageProducer.produceSendOtrosCpe(dataOtroCpe);
            }
        }

        return resp;
    }


    @GetMapping("/othercpes")
    public ResponseEntity<?> comprobantes(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            @CurrentUser UserPrincipal user) {

        return new ResponseEntity<Object>(othersVouchersService.getAllComprobantesByFiltersQuery(user, filtroDesde,
                filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage), HttpStatus.OK);
    }


    @GetMapping("/othercpes/siguienteNumero/{tipoDocumento}/{serie}")
    public ResponseEntity<?> ultimoComprobante(
            @PathVariable String tipoDocumento,
            @PathVariable String serie,
            @CurrentUser UserPrincipal user) {

        return new ResponseEntity<>(othersVouchersService.getSiguienteNumeroOtherCpe(tipoDocumento, serie, user.getRuc()), HttpStatus.OK);
    }

    @PostMapping("/othercpes/getEstadosSunat")
    public ResponseEntity<?> getEstadosSunat(@RequestBody List<Long> idsPaymentVouchers) {
        return new ResponseEntity<>(othersVouchersService.getEstadoSunatByListaIds(idsPaymentVouchers), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = {"/othercpes/enviar-sunat"}, method = RequestMethod.POST)
    public ResponsePSE enviarComprobanteASunat(
            @RequestBody IdentificadorComprobante comprobante,
            @CurrentUser UserPrincipal user,
            HttpServletRequest request) throws ServiceException {

        Map<String, Object> result;
        String authorization;
        ResponsePSE resp;

        OtherCpeEntity cpeEntity = othersVouchersService.prepareComprobanteForEnvioSunat(user.getRuc(), comprobante.getTipo(), comprobante.getSerie(), comprobante.getNumero());

        SendOtherDocumentDTO otherDocument = SendOtherDocumentDTO.builder()
                .idVoucher(cpeEntity.getIdOtroCPE())
                .nameDocument(cpeEntity.getIdentificadorDocumento())
                .ruc(cpeEntity.getNumeroDocumentoIdentidadEmisor())
                .uuidSaved(cpeEntity.getUuid())
                .tipoComprobante(cpeEntity.getTipoComprobante())
                .build();

        authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
        result = comunicationSunatService.sendOtrosCpe(
                otherDocument
        );
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

        if (resp.getEstado()) {
            messageProducer.produceEnviarCorreoOtherCpe(cpeEntity.getIdOtroCPE(), cpeEntity.getNumeroDocumentoIdentidadEmisor(), resp.getNombre());
        }

        return resp;
    }

    @ResponseBody
    @RequestMapping(value = {"/guia-remision/enviar-sunat"}, method = RequestMethod.POST)
    public ResponsePSE enviarGuiaRemisionASunat(
            @RequestBody IdentificadorComprobante comprobante,
            @CurrentUser UserPrincipal user,
            HttpServletRequest request) throws ServiceException {

        System.out.println("--------------ESTAA ENVIANDO LA GUIA A LA SUNAT");

        Map<String, Object> result;
        ResponsePSE resp;
        GuiaRemisionEntity guiaRemisionEntity = othersVouchersService.prepareGuiaForEnvioSunat(user.getRuc(),comprobante.getSerie(),comprobante.getNumero());

        SendOtherDocumentDTO guiaRemisionDocument = SendOtherDocumentDTO.builder()
                .idVoucher(guiaRemisionEntity.getIdGuiaRemision())
                .nameDocument(guiaRemisionEntity.getIdentificadorDocumento())
                .ruc(guiaRemisionEntity.getNumeroDocumentoIdentidadRemitente())
                .uuidSaved(guiaRemisionEntity.getUuid())
                .tipoComprobante(guiaRemisionEntity.getTipoComprobante())
                .serieBaja(guiaRemisionEntity.getSerieBaja())
                .numeroBaja(guiaRemisionEntity.getNumeroBaja())
                .build();

        result = comunicationSunatService.sendGuiaRemision(
                guiaRemisionDocument
        );
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

        /*if (resp.getEstado()) {
            messageProducer.produceEnviarCorreoOtherCpe(guiaRemisionEntity.getIdGuiaRemision(), guiaRemisionEntity.getIdentificadorDocumento(), resp.getNombre());
        }*/


        if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {

            GetStatusCdrDTO dataGetStatusCDR = (GetStatusCdrDTO) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
            messageProducer.produceGetStatusCDR(dataGetStatusCDR);

        }

        return resp;
    }

    @ResponseBody
    @PostMapping("/queue/sendOtrosCpe")
    public ResponsePSE sendOtrosCpe(
            @RequestBody SendOtherDocumentDTO otherDocument,
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser UserPrincipal user) {

        Map<String, Object> result;
        String authorization;
        ResponsePSE resp;

        authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
        result = comunicationSunatService.sendOtrosCpe(otherDocument);
        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {

            SendOtherDocumentDTO dataOtroCpe = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
            messageProducer.produceSendOtrosCpe(dataOtroCpe);

        }
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

        if (resp.getEstado()) {
            messageProducer.produceEnviarCorreoOtherCpe(otherDocument.getIdVoucher(), otherDocument.getRuc(), resp.getNombre());
        }

        return resp;
    }

    @ResponseBody
    @PostMapping("/queue/sendGuiaRemision")
    public ResponsePSE sendGuiaRemision(
            @RequestBody SendOtherDocumentDTO dataGuiaRemision,
            HttpServletRequest request,
            HttpServletResponse response,
            @CurrentUser UserPrincipal user) {

        Map<String, Object> result;
        String authorization;
        ResponsePSE resp;

        authorization = request.getHeader(ConstantesParameter.HEADER_AUTHORIZATION);
        result = comunicationSunatService.sendGuiaRemision(dataGuiaRemision);
        if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {
            SendOtherDocumentDTO dataGuiaRemisionSend = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
            messageProducer.produceSendGuiaRemision(dataGuiaRemisionSend);

        }
        resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

        return resp;
    }
}
