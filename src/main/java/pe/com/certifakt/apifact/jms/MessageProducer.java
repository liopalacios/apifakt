package pe.com.certifakt.apifact.jms;


import com.amazon.sqs.javamessaging.SQSMessagingClientConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.dto.EmailSendDTO;
import pe.com.certifakt.apifact.dto.GetStatusCdrDTO;
import pe.com.certifakt.apifact.dto.SendBillDTO;
import pe.com.certifakt.apifact.dto.SendOtherDocumentDTO;
import pe.com.certifakt.apifact.enums.OperacionLogEnum;
import pe.com.certifakt.apifact.enums.SubOperacionLogEnum;
import pe.com.certifakt.apifact.enums.TipoLogEnum;
import pe.com.certifakt.apifact.model.EmailSendEntity;
import pe.com.certifakt.apifact.repository.EmailSendRepository;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.Logger;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Session;
import java.io.Serializable;
import java.util.Date;


@Component
@Slf4j
public class MessageProducer {

    @Resource
    protected JmsTemplate jmsTemplate;

    @Resource
    ObjectMapper objectMapper;

    @Autowired
    EmailSendRepository emailSendRepository;

    @Value("${apifact.aws.sqs.sendBill}")
    private String sendBill;

    @Value("${apifact.aws.sqs.processVoided}")
    private String processVoided;

    @Value("${apifact.aws.sqs.processSummary}")
    private String processSummary;

    @Value("${apifact.aws.sqs.otrosCpe}")
    private String otrosCpe;

    @Value("${apifact.aws.sqs.guiaRemision}")
    private String guiaRemision;

    @Value("${apifact.aws.sqs.getStatusCdr}")
    private String getStatusCdr;

    @Value("${apifact.aws.sqs.emailSenderVoided}")
    private String emailSenderVoided;

    @Value("${apifact.aws.sqs.emailSender}")
    private String emailSender;


    @Value("${apifact.aws.sqs.emailExcelSender}")
    private String emailExcelSender;

    /*@Value("${apifact.aws.sqs.emailSenderExcel}")
    private String emailSenderExcel;*/


    @Value("${apifact.aws.sqs.emailSenderOtherCpe}")
    private String emailSenderOtherCpe;

    public void produceEnviarCorreoAnulacion(String message) {

        try {
            log.info(String.format("Enviando %s a la cola %s", message.toString(), emailSenderVoided));
            send(emailSenderVoided, message);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void produceEnviarCorreo(EmailSendDTO emailSendDTO) {

        try {
            send(emailSender, emailSendDTO);

        }catch (Exception e){
            log.error("Error",e);
        }

    }

    public void produceEnviarCorreoExcel(EmailSendDTO emailSendDTO,String userName) {

        try {

            send(emailExcelSender, emailSendDTO);

            //send(emailSenderExcel, emailSendDTO);

            EmailSendEntity email = new EmailSendEntity();

            Date date = new Date();

            email.setEmail(emailSendDTO.getEmail());
            email.setFecha(date);
            email.setUsuario(userName);
            email.setIdDowloadExcel(emailSendDTO.getId());

            emailSendRepository.save(email);

        }catch (Exception e){
            log.error("Error",e);
        }

    }

    public void produceEnviarCorreoOtherCpe(Long idOtherCpe, String ruc, String nameDocument) {

        StringBuilder msgLog = new StringBuilder();
        msgLog.append("[").append(ConstantesParameter.MSG_RESP_SUB_PROCESO_OK).append("][idPaymentVoucher:").
                append(idOtherCpe).append("][ruc:").append(ruc).append("][nameDocument:").append(nameDocument);
        try {

            send(emailSenderOtherCpe, idOtherCpe);

            Logger.register(TipoLogEnum.INFO, ruc, nameDocument,
                    OperacionLogEnum.SEND_SUNAT_VOUCHER, SubOperacionLogEnum.SEND_EMAIL, msgLog.toString());

        }catch (Exception e){
            Logger.register(TipoLogEnum.ERROR, ruc, nameDocument,
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.SEND_EMAIL,
                    e.getMessage(), msgLog.toString(), e);
        }

    }


    public void produceGetStatusCDR(GetStatusCdrDTO dataGetStatusCDR) {

        try {
            send(getStatusCdr, dataGetStatusCDR);
            log.info(">>>>>>>>>>>>Se envio a cola:"+dataGetStatusCDR.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void produceProcessSummary(String ticket, String rucEmisor) {

        try {

            send(processSummary, ticket);

            Logger.register(TipoLogEnum.INFO, rucEmisor, ticket, OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS,
                    SubOperacionLogEnum.SEND_QUEUE, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

        }catch (Exception e){

            Logger.register(TipoLogEnum.ERROR, rucEmisor, ticket, OperacionLogEnum.REGISTER_SUMMARY_DOCUMENTS,
                    SubOperacionLogEnum.SEND_QUEUE, e.getMessage(),"RucEmisor:"+rucEmisor+", Ticket:"+ticket , e);

        }

    }

    public void produceProcessVoided(String ticket, String rucEmisor) {

        try {

            send(processVoided, ticket);

            Logger.register(TipoLogEnum.INFO, rucEmisor, ticket, OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                    SubOperacionLogEnum.SEND_QUEUE, ConstantesParameter.MSG_RESP_SUB_PROCESO_OK);

        }catch (Exception e){

            Logger.register(TipoLogEnum.ERROR, rucEmisor, ticket, OperacionLogEnum.REGISTER_ANULAR_VOUCHER,
                    SubOperacionLogEnum.SEND_QUEUE, e.getMessage(),"RucEmisor:"+rucEmisor+", Ticket:"+ticket , e);

        }
    }

    public void produceSendBill(SendBillDTO dataSendBill) {

        try{
            send(sendBill, dataSendBill);
            Logger.register(TipoLogEnum.INFO, dataSendBill.getRuc(), dataSendBill.getNameDocument(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.SEND_QUEUE, "["+ConstantesParameter.MSG_RESP_SUB_PROCESO_OK
                            +"][dataSendBill:"+dataSendBill.toString()+"]");

        }catch (Exception e){
            Logger.register(TipoLogEnum.ERROR, dataSendBill.getRuc(), dataSendBill.getNameDocument(),
                    OperacionLogEnum.REGISTER_PAYMENT_VOUCHER, SubOperacionLogEnum.SEND_QUEUE,
                    e.getMessage(), dataSendBill.toString(), e);
        }

    }


    public void produceSendGuiaRemision(SendOtherDocumentDTO message) {

        try{
            send(guiaRemision, message);
            Logger.register(TipoLogEnum.INFO, message.getRuc(), message.getIdVoucher().toString(),
                    OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.SEND_QUEUE, message.toString());

        }catch (Exception e){
            Logger.register(TipoLogEnum.ERROR, message.getRuc(), message.getIdVoucher().toString(),
                    OperacionLogEnum.REGISTER_GUIA_REMISION, SubOperacionLogEnum.SEND_QUEUE,
                    e.getMessage(), message.toString(), e);

        }

    }


    public void produceSendOtrosCpe(SendOtherDocumentDTO otroCpe) {

        try {
            send(otrosCpe, otroCpe);
            log.info(">>>>>>>>>>>>Se envio a cola:"+otroCpe.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private <MESSAGE extends Serializable> void send(String queue, MESSAGE payload) {

        jmsTemplate.send(queue, new MessageCreator() {

            public javax.jms.Message createMessage(Session session) throws JMSException {
                try {
                    javax.jms.Message createMessage = session.createTextMessage(objectMapper.writeValueAsString(payload));
                    createMessage.setStringProperty(SQSMessagingClientConstants.JMSX_GROUP_ID, "messageGroup1");
                    createMessage.setStringProperty(SQSMessagingClientConstants.JMS_SQS_DEDUPLICATION_ID, "1" + System.currentTimeMillis());
                    createMessage.setStringProperty("documentType", payload.getClass().getName());
                    return createMessage;
                } catch (Exception | Error e) {
                    log.error("Fail to send message {}", payload);
                    throw new RuntimeException(e);
                }
            }
        });

    }
}
