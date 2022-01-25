package pe.com.certifakt.apifact.service;


import pe.com.certifakt.apifact.dto.EmailSendDTO;
import pe.com.certifakt.apifact.model.EmailCompanyNotifyEntity;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.util.List;

public interface EmailService {


    Boolean sendEmailOnConfirmSunat(EmailSendDTO emailSendDTO);


    Boolean sendEmailExcel(EmailSendDTO emailSendDTO);

    //Boolean sendEmailOnConfirmSunatExcel(EmailSendDTO emailSendDTO);


    Boolean sendEmailOnConfirmSunatOtherCpe(Long idPaymentVoucher, String email);

    Boolean sendEmailOnConfirmAnulacionSunat(String ticket);

    List<EmailCompanyNotifyEntity> findAllByCompanyId(String ruc);

    EmailCompanyNotifyEntity findById(Long id);

    EmailCompanyNotifyEntity save(EmailCompanyNotifyEntity emailCompanyNotifyEntity, Boolean isEdit, UserPrincipal user);

    void delete(Long id, String user);

}
