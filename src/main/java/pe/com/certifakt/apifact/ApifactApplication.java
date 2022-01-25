package pe.com.certifakt.apifact;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.MappedTypes;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import pe.com.certifakt.apifact.mapper.ComprobantesMapper;
import pe.com.certifakt.apifact.model.ComprobantesEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
@Slf4j
public class ApifactApplication {

    private final ComprobantesMapper comprobantesMapper;


    @Value("${apifact.isProduction}")
    private Boolean isProduction;

    @Value("${sunat.endpoint}")
    private String sunatEndPoint;

    @Value("${sunat.usuarioPse}")
    private String usuarioPse;

    @Value("${sunat.clavePse}")
    private String clavePse;

    @Value("${sunat.clavePseose}")
    private String clavePseose;


    // S3
    @Value("${apifact.aws.s3.bucket}")
    private String bucketName;


    // COLAS
    @Value("${apifact.aws.sqs.sendBill}")
    private String sendBillQueue;

    @Value("${apifact.aws.sqs.processVoided}")
    private String processVoidedQueue;

    @Value("${apifact.aws.sqs.processSummary}")
    private String processSummaryQueue;

    @Value("${apifact.aws.sqs.otrosCpe}")
    private String otrosCpeQueue;

    @Value("${apifact.aws.sqs.guiaRemision}")
    private String guiaRemisionQueue;

    @Value("${apifact.aws.sqs.getStatusCdr}")
    private String getStatusCdrQueue;

    @Value("${apifact.aws.sqs.emailSenderVoided}")
    private String emailSenderVoidedQueue;

    @Value("${apifact.aws.sqs.emailSender}")
    private String emailSenderQueue;

    @Value("${apifact.aws.sqs.emailSenderOtherCpe}")
    private String emailSenderOtherCpeQueue;

    @Value("${apifact.aws.sqs.emailExcelSender}")
    private String emailSenderExcelSender;


    public ApifactApplication(ComprobantesMapper comprobantesMapper){
    this.comprobantesMapper = comprobantesMapper;
    }


    public static void main(String[] args) {
        SpringApplication.run(ApifactApplication.class, args);
    }

    @PostConstruct
    public void init() {

        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));

        log.info("Spring boot application running in UTC timezone :" + new Date());
        log.info("Produccion "+ isProduction);
        log.info("Sunat EndPoint "+ sunatEndPoint);
        log.info("Usuario pse "+ usuarioPse);
        log.info("Clave pse "+ clavePse);
        log.info("Clave pse Ose "+ clavePseose);

        //s3
        log.info("S3 bucket "+ bucketName);

        //colas
        log.info("Cola sendBillQueue "+ sendBillQueue);
        log.info("Cola processVoidedQueue "+ processVoidedQueue);
        log.info("Cola processSummaryQueue "+ processSummaryQueue);
        log.info("Cola otrosCpeQueue "+ otrosCpeQueue);
        log.info("Cola guiaRemisionQueue "+ guiaRemisionQueue);
        log.info("Cola getStatusCdrQueue "+ getStatusCdrQueue);
        log.info("Cola emailSenderVoidedQueue "+ emailSenderVoidedQueue);
        log.info("Cola emailSenderQueue "+ emailSenderQueue);
        log.info("Cola emailSenderOtherCpeQueue "+ emailSenderOtherCpeQueue);
        log.info("Cola emailSenderOtherCpeQueue "+ emailSenderExcelSender);


    }

}
