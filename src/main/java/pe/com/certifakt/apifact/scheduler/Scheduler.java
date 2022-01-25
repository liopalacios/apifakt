package pe.com.certifakt.apifact.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import pe.com.certifakt.apifact.bean.EmailSexDaysDetails;
import pe.com.certifakt.apifact.bean.ResponsePSE;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.jms.MessageProducer;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.repository.PaymentVoucherRepository;
import pe.com.certifakt.apifact.service.CompanyService;
import pe.com.certifakt.apifact.service.DocumentsSummaryService;
import pe.com.certifakt.apifact.service.PaymentVoucherService;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class Scheduler {

    @Value("${apifact.isProduction}")
    private Boolean isProduction;

    @Value("${apifact.scheduler.rangoDiasSummary}")
    private Integer rangoDiasToValidar;

    @Autowired
    private PaymentVoucherService voucherService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private DocumentsSummaryService summaryService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    @Scheduled(cron = "${apifact.scheduler.cron.getPaymentsMercadoPago}")
    public void getPaymentMercadoPago() throws URISyntaxException {
        log.info("[Inicio]>>>>>>>>>>>>>>>>>>>CONSULTA DE PAGOS PENDIENTES EN MERCADO PAGO POR GET<<<<<<<<<<<<<<<<<<<");
        //Map<String,List<EmailSexDaysDetails>> objects = voucherService.getLisVoucherSeven();
        if (isProduction) {
            voucherService.getPaymentMercadoPago();
        }
    }
    @Scheduled(cron = "${apifact.scheduler.cron.generatePaymentsMercadoPago}")
    public void generatePaymentMercadoPago() throws URISyntaxException, ParseException {

        if (isProduction) {
            log.info("[Inicio]>>>>>>>>>>>>>>>>>>>GENERACION DE PAGOS  MERCADO PAGO <<<<<<<<<<<<<<<<<<<");
            voucherService.generatePaymentMercadoPago();
        }
    }
    @Scheduled(cron = "${apifact.scheduler.cron.getPaymentsPaypal}")
    public void getPaymentsPaypal() throws URISyntaxException {

        //Map<String,List<EmailSexDaysDetails>> objects = voucherService.getLisVoucherSeven();
        if (isProduction) {
            log.info("[Inicio]>>>>>>>>>>>>>>>>>>>TRAER PAGOS PAYPAL GET TOKEN<<<<<<<<<<<<<<<<<<<");
            log.info("[Inicio]>>>>>>>>>>>>>>>>>>>PRODUCCION GET TOKEN<<<<<<<<<<<<<<<<<<<");
            voucherService.getPaymentsPaypal();
        }

    }
    @Scheduled(cron = "${apifact.scheduler.cron.generatePaymentsPaypal}")
    public void generatePaymentsPaypal() throws URISyntaxException {

        //Map<String,List<EmailSexDaysDetails>> objects = voucherService.getLisVoucherSeven();
        if (isProduction) {
            log.info("[Inicio]>>>>>>>>>>>>>>>>>>>GENRAR COMPROBANTES PENDIENTES PAYPAL <<<<<<<<<<<<<<<<<<<");
            log.info("[Inicio]>>>>>>>>>>>>>>>>>>>PRODUCCION GENERATE PAYPAL <<<<<<<<<<<<<<<<<<<");
            voucherService.generatePaymentsPaypal();
        }

    }
    @Scheduled(cron = "${apifact.scheduler.cron.sendSexDay}")
    public void sendEmailSexDay() {

        //Map<String,List<EmailSexDaysDetails>> objects = voucherService.getLisVoucherSeven();
        if(isProduction){
            log.info("[Inicio]>>>>>>>>>>>>>>>>>>>TAREA PROGRAMADA PARA ENVIO CORREO SEXTO DIA<<<<<<<<<<<<<<<<<<<");
            voucherService.getLisVoucherSeven();
            voucherService.getLisVoucherSix();
            voucherService.getLisVoucherFive();
            voucherService.getLisVoucherFour();
            voucherService.getLisVoucherThree();
        }
    }
    @Scheduled(cron = "${apifact.scheduler.cron.validateLimitCompany}")
    public void validateLImitCompany() {
        if(isProduction){
            log.info("[Inicio]>>>>>>>>>>>>>>>>>>>VALIDANDO EL LIMITE DE COMPANY <<<<<<<<<<<<<<<<<<<");
            //List<CompanyEntity> companyEntities = new ArrayList<>();
            //if(isProduction)
           // companyService.getAllLimitCompany();
           // for (CompanyEntity entity: companyEntities) {
           //     entity.setEstado("D");
          //  }
        }
    }
    @Scheduled(cron = "${apifact.scheduler.cron.sendSevenDay}")
    public void sendSevenDay() {

        //Map<String,List<EmailSexDaysDetails>> objects = voucherService.getLisVoucherSeven();
        if(isProduction){
            log.info("[Inicio]>>>>>>>>>>>>>>>>>>>TAREA PROGRAMADA PARA ENVIO SUNAT SEPTIMO DIA<<<<<<<<<<<<<<<<<<<");
            Map<String,String> stringMap = voucherService.postVoucherSeven();
            if(stringMap.size()>0){
                voucherService.sendEmailReportSevenDay(stringMap);
            }
            Map<String,String> stringMap2 = voucherService.postVoucherSix();
            if(stringMap2.size()>0){
                voucherService.sendEmailReportSixDay(stringMap2);
            }
            Map<String,String> stringMap3 = voucherService.postVoucherFive();
            if(stringMap3.size()>0){
                voucherService.sendEmailReportFiveDay(stringMap3);
            }
            Map<String,String> stringMap4 = voucherService.postVoucherFour();
            if(stringMap4.size()>0){
                voucherService.sendEmailReportFourDay(stringMap4);
            }
            Map<String,String> stringMap5 = voucherService.postVoucherThree();
            if(stringMap5.size()>0){
                voucherService.sendEmailReportThreeDay(stringMap5);
            }
        }


    }

    public void testDto() {

            log.info("[Inicio]>>>>>>>>>>>>>>>>>>> TEST DTO TEST DTO <<<<<<<<<<<<<<<<<<");
        List<PaymentVoucherInterDto> facturasNoEnviadasCon7Dias = new ArrayList<>();
        //List<PaymentVoucherInterDto> facturasNoEnviadasCon7Dias = voucherService.getFacturasNoEnviadasCon7Dias();

        for (PaymentVoucherInterDto p : facturasNoEnviadasCon7Dias){
            System.out.println(p.getId());
            System.out.println(p.getEmisor());
            System.out.println(p.getFechaEmision());
            System.out.println(p.getIdentificador());
        }

    }

    @Scheduled(cron = "${apifact.scheduler.cron.summaryBoletasDocuments}")
    public void executeSummaryBoletasDocuments() {


        if (isProduction){
            log.info("[Inicio]>>>>>>>>>>>>>>>>>>>TAREA PROGRAMADA PARA GENERACION DE RESUMEN DIARIO DE BOLETAS<<<<<<<<<<<<<<<<<<<");
            System.out.println(getDiaAnterior());
            List<String> listRucs = paymentVoucherRepository.getCompaniesWithBoleta01(getDiaAnterior());
            for (String company : listRucs) {
                    List<String> listFechas = paymentVoucherRepository.getFechasWithBoleta01(getDiaAnterior(),company);
                    for(String fechaEmision: listFechas){
                        try {
                            ResponsePSE resp = summaryService.generarSummaryByFechaEmisionAndRuc(
                                    company,
                                    fechaEmision,
                                    null, "Job"
                            );
                            if (resp.getEstado()) {
                                messageProducer.produceProcessSummary(resp.getTicket(), company);
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
            }
        }
    }

    @Scheduled(cron = "${apifact.scheduler.cron.summaryNotaCreditoDocuments}")
    public void executeSummaryNotaCreditoDocuments() {

        if (isProduction){
            log.info("[Inicio]>>>>>>TAREA RESUMEN OTAS DE CREDITO<<<<<<<<<<<<<<<<<<<");

            List<String> listFechas = summaryService.getFechasPendientesNotas();
            List<String> listRucPendientes = summaryService.getRucsPendientesNotas();
            for (String company : listRucPendientes) {
                for (int j = 0; j < listFechas.size(); j++) {
                    try {
                        String fechaEmision = listFechas.get(j);
                        ResponsePSE resp = summaryService.generarSummaryNotaCreditoByFechaEmisionAndRuc(
                                company,
                                fechaEmision,
                                null, "Job"
                        );
                        if (resp.getEstado()) {
                            messageProducer.produceProcessSummary(resp.getTicket(), company);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    private String getFechaEmision(int dias) {
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.add(Calendar.DATE, -(dias + 1));
        String fechaEmision = UtilFormat.formatoYYYYmmDD(fechaEmisionCalendar.getTime());
        return fechaEmision;
    }

    private String getDiaAnterior() {
        Calendar fechaEmisionCalendar = Calendar.getInstance();
        fechaEmisionCalendar.add(Calendar.DATE, -1);
        String fechaEmision = UtilFormat.formatoYYYYmmDD(fechaEmisionCalendar.getTime());
        return fechaEmision;
    }
}
