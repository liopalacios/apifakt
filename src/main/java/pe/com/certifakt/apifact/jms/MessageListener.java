package pe.com.certifakt.apifact.jms;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.bean.ResponsePSE;
import pe.com.certifakt.apifact.dto.EmailSendDTO;
import pe.com.certifakt.apifact.dto.GetStatusCdrDTO;
import pe.com.certifakt.apifact.dto.SendBillDTO;
import pe.com.certifakt.apifact.dto.SendOtherDocumentDTO;
import pe.com.certifakt.apifact.service.ComunicationSunatService;
import pe.com.certifakt.apifact.service.DocumentsSummaryService;
import pe.com.certifakt.apifact.service.DocumentsVoidedService;
import pe.com.certifakt.apifact.service.EmailService;
import pe.com.certifakt.apifact.util.ConstantesParameter;

import java.util.Map;


@Component
@Slf4j
@AllArgsConstructor
public class MessageListener {

    private final ComunicationSunatService comunicationSunatService;
    private final MessageProducer messageProducer;
    private final EmailService emailService;
    private final DocumentsVoidedService documentsVoidedService;
    private final DocumentsSummaryService documentsSummaryService;

    @JmsListener(destination = "${apifact.aws.sqs.sendBill}")
    public void receiveMessageQueueSendBill(@Payload final Message<SendBillDTO> message) {
        log.info("Se recibio mensaje >>>>>>>>>>>>>>> " + message.toString());

        Map<String, Object> result;
        ResponsePSE resp;

        try {
            Thread.sleep(2000);
            SendBillDTO sendBillDTO = message.getPayload();
            result = comunicationSunatService.sendDocumentBill(
                    sendBillDTO.getRuc(),
                    sendBillDTO.getIdPaymentVoucher()
            );
            resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

            if (resp.getEstado()) {
                messageProducer.produceEnviarCorreo(EmailSendDTO.builder().id(sendBillDTO.getIdPaymentVoucher()).build());
            }


            if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {

                GetStatusCdrDTO dataGetStatusCDR = (GetStatusCdrDTO) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
                messageProducer.produceGetStatusCDR(dataGetStatusCDR);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "${apifact.aws.sqs.otrosCpe}")
    public void receiveMessageQueueOtrosCpe(@Payload final Message<SendOtherDocumentDTO> message) {
        log.info("Se recibio mensaje >>>>>>>>>>>>>>> " + message.toString());

       try {
           Map<String, Object> result;
           ResponsePSE resp;

           result = comunicationSunatService.sendOtrosCpe(message.getPayload());
           if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {

               SendOtherDocumentDTO dataOtroCpe = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
               messageProducer.produceSendOtrosCpe(dataOtroCpe);

           }
           resp = (ResponsePSE) result.get(ConstantesParameter.PARAM_BEAN_RESPONSE_PSE);

           if (resp.getEstado()) {
               messageProducer.produceEnviarCorreoOtherCpe(message.getPayload().getIdVoucher(), message.getPayload().getRuc(), resp.getNombre());
           }

       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    @JmsListener(destination = "${apifact.aws.sqs.guiaRemision}")
    public void receiveMessageQueueGuiaRemision(@Payload final Message<SendOtherDocumentDTO> message) {
        log.info("Se recibio mensaje >>>>>>>>>>>>>>>" + message.toString());
        try {
            Map<String, Object> result;
            result = comunicationSunatService.sendGuiaRemision(message.getPayload());
            if (result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE) != null) {
                SendOtherDocumentDTO dataGuiaRemisionSend = (SendOtherDocumentDTO) result.get(ConstantesParameter.PARAM_BEAN_SEND_OTRO_CPE);
                messageProducer.produceSendGuiaRemision(dataGuiaRemisionSend);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "${apifact.aws.sqs.getStatusCdr}")
    public void receiveMessageQueueGetStatusCDR(@Payload final Message<GetStatusCdrDTO> message) {
        log.info("Se recibio mensaje >>>>>>>>>>>>>>> " + message.toString());

       try {
           Map<String, Object> result;

           result = comunicationSunatService.getStatusBill(
                   message.getPayload()
           );
           if (result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR) != null) {

               GetStatusCdrDTO dataGetStatusCDR = (GetStatusCdrDTO) result.get(ConstantesParameter.PARAM_BEAN_GET_STATUS_CDR);
               messageProducer.produceGetStatusCDR(dataGetStatusCDR);

           }
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    @JmsListener(destination = "${apifact.aws.sqs.emailSender}")
    public void receiveMessageQueueEmailSender(@Payload final Message<EmailSendDTO> message) {
        log.info("Se recibio mensaje Para enviar correo Electronico >>>>>>>>>>>>>>> " + message.getPayload());
      try {
          emailService.sendEmailOnConfirmSunat(message.getPayload());
      }catch (Exception e) {
          e.printStackTrace();
      }
    }


    @JmsListener(destination = "${apifact.aws.sqs.emailExcelSender}")
    public void receiveMessageExcelEmailSender(@Payload final Message<EmailSendDTO> message) {
        log.info("Se recibio mensaje Para enviar correo Electronico >>>>>>>>>>>>>>> " + message.getPayload());
        try {
            emailService.sendEmailExcel(message.getPayload());
        } catch (Exception e) {}}

    /*@JmsListener(destination = "${apifact.aws.sqs.emailSenderExcel}")
    public void receiveMessageQueueEmailSenderExcel(@Payload final Message<EmailSendDTO> message) {
        log.info("Se recibio mensaje Para enviar correo Electronico de Excel >>>>>>>>>>>>>>> " + message.getPayload());
        try {
            emailService.sendEmailOnConfirmSunatExcel(message.getPayload());
        }catch (Exception e) {

            e.printStackTrace();
        }
    }*/

    @JmsListener(destination = "${apifact.aws.sqs.emailSenderVoided}")
    public void receiveMessageQueueEmailSenderVoided(@Payload final Message<String> message) {
        log.info("Se recibio mensaje Para enviar correo Electronico de Baja >>>>>>>>>>>>>>> " + message);
       try {
           emailService.sendEmailOnConfirmAnulacionSunat(message.getPayload());
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    @JmsListener(destination = "${apifact.aws.sqs.processVoided}")
    public void receiveMessageQueueProcessVoided(@Payload final Message<String> message) {
        log.info("Se recibio mensaje Para Procesar baja >>>>>>>>>>>>>>> " + message);

        try {
            Thread.sleep(3000);
            String userName = ConstantesParameter.USER_API_QUEUE;
            String rucEmisor = null;
            Boolean isProcessTicketVoided = documentsVoidedService.processVoidedTicket(
                    message.getPayload(),
                    userName,
                    rucEmisor);

            //SI ESTA EN PROCESO, LO VUELVO A ENCOLAR
            if (!isProcessTicketVoided) {
                log.info("EL TICKET AUN NO ESTA PROCESADO, ENCOLANDO NUEVAMENTE " + message.getPayload());
                messageProducer.produceProcessVoided(message.getPayload(), rucEmisor);
            } else {
                log.info("TICKET PROCESADO " + message.getPayload());
                messageProducer.produceEnviarCorreoAnulacion(message.getPayload());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @JmsListener(destination = "${apifact.aws.sqs.processSummary}")
    public void receiveMessageQueueProcessSummary(@Payload final Message<String> message) {
        log.info("Se recibio mensaje Para Procesar resumen diario >>>>>>>>>>>>>>>>>> " + message);

        ResponsePSE resp = new ResponsePSE();
        try {
            String userName = ConstantesParameter.USER_API_QUEUE;
            String rucEmisor = null;
            resp = documentsSummaryService.processSummaryTicket(message.getPayload(), userName, rucEmisor);

            if (resp != null && !resp.getEstado() && resp.getRespuesta().equals(ConstantesParameter.STATE_SUMMARY_VOIDED_DOCUMENTS_IN_PROCESO)) {

                if (resp.getIntentosGetStatus() < 5) {
                    log.info("El ticket sigue en proceso, reenviando getstatus a la cola {}", message.getPayload());
                    messageProducer.produceProcessSummary(message.getPayload(), rucEmisor);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
