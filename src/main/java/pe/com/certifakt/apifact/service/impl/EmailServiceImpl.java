package pe.com.certifakt.apifact.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.config.MailTemplateService;
import pe.com.certifakt.apifact.dto.EmailSendDTO;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.*;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.AmazonS3ClientService;
import pe.com.certifakt.apifact.service.EmailService;
import pe.com.certifakt.apifact.service.ReportService;
import pe.com.certifakt.apifact.util.ConstantesUtils;
import pe.com.certifakt.apifact.util.StringsUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${apifact.email}")
    private String emailFrom;

    @Value("${urlspublicas.consultaComprobante}")
    private String urlConsultaComprobante;

    @Value("${urlspublicas.imagenes}")
    private String urlImagenes;

    @Autowired
    private AmazonS3ClientService amazonS3ClientService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DowloadExcelRepository dowloadExcelRepository;

    @Autowired
    private MailTemplateService mailTemplateService;

    @Autowired
    private PaymentVoucherRepository paymentVoucherRepository;

    @Autowired
    private OtherCpeRepository otherCpeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmailCompanyNotifyRepository emailCompanyNotifyRepository;

    @Autowired
    private DocumentsVoidedRepository documentsVoidedRepository;

    @Autowired
    private EmailSendRepository emailSendRepository;





    @Override
    @Transactional
    public Boolean sendEmailOnConfirmSunat(EmailSendDTO emailSendDTO) {

        try {
            PaymentVoucherEntity comprobante = paymentVoucherRepository.findById(emailSendDTO.getId()).get();
            CompanyEntity companyEntity = companyRepository.findByRuc(comprobante.getRucEmisor());
            List<String> emailsVoucher = new ArrayList<>();

            if (emailSendDTO.getEmail() != null) {
                String[] emails = (emailSendDTO.getEmail()).split(",");
                //emailToSend = emailSendDTO.getEmail();
                for (int i=0;i<emails.length;i++){
                    emailsVoucher.add(emails[i].trim());
                }
                //emailsVoucher.add(emailSendDTO.getEmail());
            }else {
                String[] emails = (comprobante.getEmailReceptor()==null?"":comprobante.getEmailReceptor()).split(",");
                for (int i=0;i<emails.length;i++){
                    emailsVoucher.add(emails[i].trim());
                }
            } //emailToSend = comprobante.getEmailReceptor();

            //EMAIL ADICIONALES
            List<EmailCompanyNotifyEntity> emailsAdicionalesNotificar = emailCompanyNotifyRepository.findAllByCompany_RucAndEstadoIsTrue(comprobante.getRucEmisor());
            List<String> emailsList = new ArrayList<>();
            if (!emailsAdicionalesNotificar.isEmpty() && (emailSendDTO.getEmail() == null || ((emailSendDTO.getEmail().trim()).length()==0) )){

                emailsList = emailsAdicionalesNotificar.stream().filter(e -> (e.getEmail().trim()).length()>0)
                        .map(e -> e.getEmail()).collect(Collectors.toList());
            }

            /*if (emailToSend != null && !emailToSend.isEmpty())
                emailsList.add(emailToSend);*/
            for (int j=0;j<emailsVoucher.size();j++){
                emailsList.add(emailsVoucher.get(j));
            }
            //for (String emailSend : emailsList) {
                if (emailsList.size()>0) {
                    //log.info(emailsList);
                    List<String> emailListFinal = emailsList;
                    System.out.print(emailListFinal);
                    MimeMessagePreparator preparator = new MimeMessagePreparator() {
                        public void prepare(MimeMessage mimeMessage) throws Exception {
                            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                            String textMensajeHtml = mailTemplateService.getFreeMarkerTemplateContent(
                                    new HashMap<String, Object>() {{
                                        put("urlLogo", companyEntity.getArchivoLogo() == null ? "" : (urlImagenes + companyEntity.getArchivoLogo().getIdRegisterFileSend()));
                                        put("nombreReceptor", comprobante.getDenominacionReceptor());
                                        put("nombreDocumento", StringsUtils.getNombreCortoTipoComprobante(comprobante.getTipoComprobante()));
                                        put("serie", comprobante.getSerie());
                                        put("numero", comprobante.getNumero().toString());
                                        put("urlConsultaComprobante", urlConsultaComprobante + companyEntity.getRuc());
                                        put("nombreEmpresa", companyEntity.getRazonSocial() != null ? companyEntity.getRazonSocial() : "");
                                        put("nombreComercial", companyEntity.getNombreComercial() != null ? companyEntity.getNombreComercial() : "");
                                        put("rucEmpresa", companyEntity.getRuc() != null ? companyEntity.getRuc() : "");
                                        put("direccionEmpresa", companyEntity.getDireccion() != null ? companyEntity.getDireccion() : "");
                                    }}, "templateMailConfirmMessage.txt"
                            );
                            helper.setSubject(StringsUtils.getNombreCortoTipoComprobante(comprobante.getTipoComprobante()) + " " + comprobante.getSerie() + "-" + comprobante.getNumero().toString() + " " + companyEntity.getRazonSocial());

                            helper.setFrom(new InternetAddress(emailFrom, companyEntity.getRazonSocial()));
                            String[] stringArray = new String[emailListFinal.size()];
                            for (int j = 0; j < emailListFinal.size(); j++) {
                                stringArray[j] = emailListFinal.get(j);
                            }
                            helper.setTo(stringArray);
                            helper.setText(textMensajeHtml, true);
                            if (companyEntity.getEmail() != null) {
                                helper.setReplyTo(new InternetAddress(companyEntity.getEmail(), companyEntity.getRazonSocial()));
                            }


                            String nombreComprobante = String.format("%s-%s-%s-%s", comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero().toString());

                            InputStream isXml = extractXmlFromZip(amazonS3ClientService.downloadFileStorage(comprobante.getXmlActivo()), nombreComprobante + ".xml");


                            if (companyEntity.getSendticket() == 1) {
                                InputStream isTicket = reportService.getPdfComprobanteTicket(comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
                                helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isTicket)));
                            } else {
                                InputStream isPdf = reportService.getPdfComprobanteA4(comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
                                helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isPdf)));
                            }

                            helper.addAttachment(nombreComprobante + ".xml", new ByteArrayResource(IOUtils.toByteArray(isXml)));

                        }
                    };

                    emailSender.send(preparator);

                }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean sendEmailExcel(EmailSendDTO emailSendDTO) {


        try {

            DowloadExcelEntity dowloadExcelEntity = dowloadExcelRepository.findByIdExcelDocument(emailSendDTO.getId());
            CompanyEntity companyEntity = companyRepository.findById(dowloadExcelEntity.getCodCompany()).get();

            if(companyEntity==null)
                return false;

            String emailToSend = "";
                emailToSend = emailSendDTO.getEmail();

            //EMAIL ADICIONALES
            List<String> emailsList = new ArrayList<>();
            emailsList.add(emailToSend);

            for (String emailSend : emailsList) {
                if (StringsUtils.validateEmail(emailSend)) {
                    List<String> emailListFinal = emailsList;
                    MimeMessagePreparator preparator = new MimeMessagePreparator() {
                        public void prepare(MimeMessage mimeMessage) throws Exception {
                            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                            String textMensajeHtml = mailTemplateService.getFreeMarkerTemplateContent(
                                    new HashMap<String, Object>() {{
                                        put("nombreComercial", companyEntity.getNombreComercial() != null ? companyEntity.getNombreComercial() : "");
                                        put("rucEmpresa", companyEntity.getRuc() != null ? companyEntity.getRuc() : "");
                                        put("direccionEmpresa", companyEntity.getDireccion() != null ? companyEntity.getDireccion() : "");
                                    }}, "templateMailConfirmOtherCpeMessageExcel.txt"
                            );
                            String nombreDocumento = String.format(dowloadExcelEntity.getIdentificador());
                            helper.setSubject("Reporte "+nombreDocumento+companyEntity.getRazonSocial());
                            helper.setFrom(new InternetAddress(emailFrom, companyEntity.getRazonSocial()));
                            String[] stringArray = new String[emailListFinal.size()];

                            for (int j = 0; j < emailListFinal.size(); j++) {
                                stringArray[j] = emailListFinal.get(j);
                            }
                            helper.setTo(stringArray);
                            helper.setText(textMensajeHtml, true);
                            if (companyEntity.getEmail() != null) {
                                helper.setReplyTo(new InternetAddress(companyEntity.getEmail(), companyEntity.getRazonSocial()));
                            }

                            ByteArrayInputStream isXLS =null;
                             isXLS = amazonS3ClientService.downloadFileStorage(dowloadExcelEntity.getXlsActivo());
                            helper.addAttachment(nombreDocumento + ".xls", new ByteArrayResource(IOUtils.toByteArray(isXLS)));
                        }
                    };

                    emailSender.send(preparator);

                }

                return true;

            }


        }catch(Exception e)
        {
        e.printStackTrace();
        }
        return false;

    }

    //@Override
    /*public Boolean sendEmailOnConfirmSunatExcel(EmailSendDTO emailSendDTO) {
        try {
            DowloadExcelEntity excel = dowloadExcelRepository.findById(emailSendDTO.getId()).get();
            CompanyEntity companyEntity = companyRepository.findById(excel.getCodCompany()).get();

            String emailToSend = "";

            emailToSend = emailSendDTO.getEmail();
            System.out.println("ruc emisor: "+excel.getIdentificador().substring(0,11));
            //EMAIL ADICIONALES
            List<EmailCompanyNotifyEntity> emailsAdicionalesNotificar = emailCompanyNotifyRepository.findAllByCompany_RucAndEstadoIsTrue(excel.getIdentificador().substring(0,12));
            List<String> emailsList = new ArrayList<>();
            if (!emailsAdicionalesNotificar.isEmpty() && emailSendDTO.getEmail() == null)
                emailsList = emailsAdicionalesNotificar.stream().map(e -> e.getEmail()).collect(Collectors.toList());
            if (emailToSend != null && !emailToSend.isEmpty())
                emailsList.add(emailToSend);

            //for (String emailSend : emailsList) {
            //if (StringsUtils.validateEmail(emailSend)) {
            //log.info(emailsList);
            List<String> emailListFinal = emailsList;
            System.out.println("emailListFinal: "+emailListFinal);
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                    String textMensajeHtml = mailTemplateService.getFreeMarkerTemplateContent(
                            new HashMap<String, Object>() {{
                                put("urlLogo", companyEntity.getArchivoLogo() == null ? "" : (urlImagenes + companyEntity.getArchivoLogo().getIdRegisterFileSend()));
                                put("nombreReceptor", companyEntity.getNombreComercial()); //Quitamos denominacion del receptor
                                put("nombreDocumento", StringsUtils.getNombreCortoTipoComprobante(excel.getTipoDocumento()));
                                put("serie", excel.getSerie());
                                //put("numero", comprobante.getNumero().toString()); //Quitamos numero para el excel
                                put("urlConsultaComprobante", urlConsultaComprobante + companyEntity.getRuc());
                                put("nombreEmpresa", companyEntity.getRazonSocial() != null ? companyEntity.getRazonSocial() : "");
                                put("nombreComercial", companyEntity.getNombreComercial() != null ? companyEntity.getNombreComercial() : "");
                                put("rucEmpresa", companyEntity.getRuc() != null ? companyEntity.getRuc() : "");
                                put("direccionEmpresa", companyEntity.getDireccion() != null ? companyEntity.getDireccion() : "");
                            }}, "templateMailConfirmMessageExcel.txt"
                    );
                    helper.setSubject(StringsUtils.getNombreCortoTipoComprobante(excel.getTipoDocumento())+" "+excel.getSerie()+" "+companyEntity.getRazonSocial());

                    helper.setFrom(new InternetAddress(emailFrom, companyEntity.getRazonSocial()));
                    String[] stringArray = new String[emailListFinal.size()];
                    for(int j =0;j<emailListFinal.size();j++){
                        stringArray[j] = emailListFinal.get(j);
                    }
                    helper.setTo(stringArray);
                    helper.setText(textMensajeHtml, true);
                    if (companyEntity.getEmail() != null) {
                        helper.setReplyTo(new InternetAddress(companyEntity.getEmail(), companyEntity.getRazonSocial()));
                    }


                    String nombreComprobante = String.format("%s-%s-%s", excel.getIdentificador().substring(0,12), excel.getTipoDocumento(), excel.getSerie());

                    //InputStream isXls = extractXlsFromZip(amazonS3ClientService.downloadFileStorage(excel.getXlsActivo()), excel.getIdentificador()+".xls");
                    System.out.println("doloadFileStorage: "+amazonS3ClientService.downloadFileStorage(excel.getXlsActivo()));
                    System.out.println("XlsActivo: "+excel.getXlsActivo());
                    //System.out.println("isXls: "+isXls);
                    System.out.println("nombreComprobante: "+excel.getIdentificador()+".xls");
                    /*if (companyEntity.getSendticket() == 1) {
                        InputStream isTicket = reportService.getPdfComprobanteTicket(comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
                        helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isTicket)));
                    } else {
                        InputStream isPdf = reportService.getPdfComprobanteA4(comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
                        helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isPdf)));
                    }*/

                    /*helper.addAttachment(nombreComprobante + ".xls", new ByteArrayResource(IOUtils.toByteArray(amazonS3ClientService.downloadFileStorage(excel.getXlsActivo()))));

                }
            };

            emailSender.send(preparator);

            //}

            //}

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/





    private ByteArrayInputStream extractXmlFromZip(ByteArrayInputStream zipBis, String zipEntryName) throws IOException {
        try (ZipInputStream zipin = new ZipInputStream(zipBis)) {
            ZipEntry ze;
            while ((ze = zipin.getNextEntry()) != null) {
                String zeName = ze.getName();
                if (zipEntryName.equals(zeName)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int b = zipin.read();
                    while (b >= 0) {
                        baos.write(b);
                        b = zipin.read();
                    }
                    zipin.close();
                    return new ByteArrayInputStream(baos.toByteArray());
                }
            }
            zipin.close();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public Boolean sendEmailOnConfirmSunatOtherCpe(Long idOtherCpe, String emailFromWebApp) {

        try {

            OtherCpeEntity comprobante = otherCpeRepository.findById(idOtherCpe).get();
            CompanyEntity companyEntity = companyRepository.findByRuc(comprobante.getNumeroDocumentoIdentidadEmisor());

            String emailToSend = "";
            if (emailFromWebApp != null)
                emailToSend = emailFromWebApp;
            else emailToSend = comprobante.getEmailReceptor();

            //EMAIL ADICIONALES
            List<EmailCompanyNotifyEntity> emailsAdicionalesNotificar = emailCompanyNotifyRepository.findAllByCompany_RucAndEstadoIsTrue(comprobante.getNumeroDocumentoIdentidadEmisor());
            List<String> emailsList = new ArrayList<>();
            if (!emailsAdicionalesNotificar.isEmpty() && emailFromWebApp == null)
                emailsList = emailsAdicionalesNotificar.stream().map(e -> e.getEmail()).collect(Collectors.toList());
            if (emailToSend != null && !emailToSend.isEmpty())
                emailsList.add(emailToSend);

            for (String emailSend : emailsList) {
                if (StringsUtils.validateEmail(emailSend)) {
                    log.info("Email validado " + emailSend);
                    String finalEmailToSend = emailSend;
                    MimeMessagePreparator preparator = new MimeMessagePreparator() {
                        public void prepare(MimeMessage mimeMessage) throws Exception {
                            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                            String textMensajeHtml = mailTemplateService.getFreeMarkerTemplateContent(
                                    new HashMap<String, Object>() {{
                                        put("urlLogo", companyEntity.getArchivoLogo() == null ? "" : (urlImagenes + companyEntity.getArchivoLogo().getIdRegisterFileSend()));
                                        put("nombreReceptor", comprobante.getDenominacionReceptor());
                                        put("nombreDocumento", StringsUtils.getNombreCortoTipoComprobante(comprobante.getTipoComprobante()));
                                        put("serie", comprobante.getSerie());
                                        put("numero", comprobante.getNumero().toString());
                                        put("nombreEmpresa", companyEntity.getRazonSocial() != null ? companyEntity.getRazonSocial() : "");
                                        put("nombreComercial", companyEntity.getNombreComercial() != null ? companyEntity.getNombreComercial() : "");
                                        put("rucEmpresa", companyEntity.getRuc() != null ? companyEntity.getRuc() : "");
                                        put("direccionEmpresa", companyEntity.getDireccion() != null ? companyEntity.getDireccion() : "");
                                    }}, "templateMailConfirmOtherCpeMessage.txt"
                            );

                            helper.setSubject(StringsUtils.getNombreCortoTipoComprobante(comprobante.getTipoComprobante())+" "+comprobante.getSerie()+"-"+comprobante.getNumero().toString()+" "+companyEntity.getRazonSocial());
                            helper.setFrom(emailFrom);
                            helper.setTo(finalEmailToSend);
                            helper.setText(textMensajeHtml, true);

                            String nombreComprobante = String.format("%s-%s-%s-%s", comprobante.getNumeroDocumentoIdentidadEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero().toString());

                            InputStream isXml = amazonS3ClientService.downloadFileStorage(comprobante.getXmlActivo());
                            InputStream isPdf = reportService.getPdfComprobanteOtherCpe(comprobante.getNumeroDocumentoIdentidadEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());

                            helper.addAttachment(nombreComprobante + ".zip", new ByteArrayResource(IOUtils.toByteArray(isXml)));
                            helper.addAttachment(nombreComprobante + ".pdf", new ByteArrayResource(IOUtils.toByteArray(isPdf)));
                        }
                    };

                    emailSender.send(preparator);

                }

            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Boolean sendEmailOnConfirmAnulacionSunat(String ticket) {

        try {
            VoidedDocumentsEntity voided = documentsVoidedRepository.findByTicketSunat(ticket);

            if (voided.getEstado().equals(ConstantesUtils.STATE_SUMMARY_VOIDED_DOCUMENTS_PROCESO_OK)) {
                CompanyEntity companyEntity = companyRepository.findByRuc(voided.getRucEmisor());
                //EMAIL ADICIONALES
                List<EmailCompanyNotifyEntity> emailsAdicionalesNotificar = emailCompanyNotifyRepository.findAllByCompany_RucAndEstadoIsTrue(voided.getRucEmisor());
                List<String> emailsList = new ArrayList<>();
                if (!emailsAdicionalesNotificar.isEmpty())
                    emailsList = emailsAdicionalesNotificar.stream().map(e -> e.getEmail()).collect(Collectors.toList());

                List<DetailDocsVoidedEntity> bajasComprobantes = voided.getDetailBajaDocumentos();

                for (DetailDocsVoidedEntity dbaja : bajasComprobantes) {

                    PaymentVoucherEntity comprobante = paymentVoucherRepository.findByRucEmisorAndTipoComprobanteAndSerieAndNumeroOrderByDetailsPaymentVouchers_NumeroItemAsc(voided.getRucEmisor(), dbaja.getTipoComprobante(), dbaja.getSerieDocumento(), dbaja.getNumeroDocumento());

                    for (String emailSend : emailsList) {
                        if (StringsUtils.validateEmail(emailSend)) {
                            log.info("Email validado " + emailSend);
                            String finalEmailToSend = emailSend;
                            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                                public void prepare(MimeMessage mimeMessage) throws Exception {
                                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                                    String textMensajeHtml = mailTemplateService.getFreeMarkerTemplateContent(
                                            new HashMap<String, Object>() {{
                                                put("urlLogo", companyEntity.getArchivoLogo() == null ? "" : (urlImagenes + companyEntity.getArchivoLogo().getIdRegisterFileSend()));
                                                put("nombreDocumento", StringsUtils.getNombreCortoTipoComprobante(comprobante.getTipoComprobante()));
                                                put("serie", comprobante.getSerie());
                                                put("numero", comprobante.getNumero().toString());
                                                put("nombreEmpresa", companyEntity.getRazonSocial() != null ? companyEntity.getRazonSocial() : "");
                                                put("nombreComercial", companyEntity.getNombreComercial() != null ? companyEntity.getNombreComercial() : "");
                                                put("rucEmpresa", companyEntity.getRuc() != null ? companyEntity.getRuc() : "");
                                                put("direccionEmpresa", companyEntity.getDireccion() != null ? companyEntity.getDireccion() : "");
                                            }}, "templateMailConfirmAnulacionComprobanteMessage.txt"
                                    );

                                    helper.setSubject("Notificación de anulación");
                                    helper.setFrom(emailFrom);
                                    helper.setTo(finalEmailToSend);
                                    helper.setText(textMensajeHtml, true);

                                    String nombreCdrBaja = String.format("%s-%s-%s", "R", voided.getRucEmisor(), voided.getIdDocument());

                                    InputStream isCdrBaja = extractXmlFromZip(amazonS3ClientService.downloadFileStorage(comprobante.getCdrActivo()), nombreCdrBaja + ".xml");

                                    helper.addAttachment(nombreCdrBaja + ".xml", new ByteArrayResource(IOUtils.toByteArray(isCdrBaja)));
                                }
                            };

                            emailSender.send(preparator);

                        }

                    }

                }

                return true;
            } else return false;

        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public List<EmailCompanyNotifyEntity> findAllByCompanyId(String ruc) {

        return emailCompanyNotifyRepository.findAllByCompany_RucAndEstadoIsTrue(ruc);
    }

    @Override
    public EmailCompanyNotifyEntity findById(Long id) {
        return emailCompanyNotifyRepository.findById(id).get();
    }

    @Override
    public EmailCompanyNotifyEntity save(EmailCompanyNotifyEntity email, Boolean isEdit, UserPrincipal user) {
        CompanyEntity companyEntity = companyRepository.findByRuc(user.getRuc());
        email.setCompany(companyEntity);
        emailCompanyNotifyRepository.save(email);
        return email;
    }

    @Override
    public void delete(Long id, String user) {
        EmailCompanyNotifyEntity email = emailCompanyNotifyRepository.findById(id).get();
        email.setEstado(false);
        emailCompanyNotifyRepository.save(email);
    }


}
