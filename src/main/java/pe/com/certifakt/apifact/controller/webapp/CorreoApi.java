package pe.com.certifakt.apifact.controller.webapp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.dto.EmailSendDTO;
import pe.com.certifakt.apifact.jms.MessageProducer;
import pe.com.certifakt.apifact.model.EmailCompanyNotifyEntity;
import pe.com.certifakt.apifact.security.CurrentUser;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.EmailService;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api")
public class CorreoApi {


    private final EmailService emailService;
    private final MessageProducer messageProducer;


    @PostMapping("/enviarCorreo/{idPaymentVoucher}")
    public Boolean enviar(@PathVariable Long idPaymentVoucher, @RequestParam(required = false) String email) {

        messageProducer.produceEnviarCorreo(EmailSendDTO.builder()
                .id(idPaymentVoucher)
                .email(email)
                .build());

    return true;
    }


  /*  @PostMapping("/enviarCorreoExcel/{idExcelDocument}")
    public Boolean enviarExcel(@PathVariable Long idExcelDocument, @RequestParam(required = false) String email) {
        messageProducer.produceEnviarCorreoExcel(EmailSendDTO.builder()
                .id(idExcelDocument)
                .email(email)
                .build());}*/

    @PostMapping("/enviarCorreoExcel/{idDowloadExcel}")
    public Boolean enviarExcel(@PathVariable Long idDowloadExcel, @RequestParam(required = false) String email,@CurrentUser UserPrincipal user) {

        messageProducer.produceEnviarCorreoExcel(EmailSendDTO.builder()
                .id(idDowloadExcel)
                .email(email)
                .build(),user.getUsername());

        return true;
    }

    @PostMapping("/enviarCorreoOtherCpe/{idOtherCpe}")
    public Boolean enviarOtherCpe(@PathVariable Long idOtherCpe, @RequestParam(required = false) String email) {
        String emailToSend = null;
        emailToSend = email != null ? email : null;

        log.info(emailToSend);
        return emailService.sendEmailOnConfirmSunatOtherCpe(idOtherCpe, emailToSend);

    }



    @PostMapping("/enviarCorreoAnulacion/{ticket}")
    public Boolean enviarCorreoAnulacion(@PathVariable String ticket) {

        return emailService.sendEmailOnConfirmAnulacionSunat(ticket);

    }


    @GetMapping("/emails")
    public ResponseEntity<?> emails(@CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(emailService.findAllByCompanyId(user.getRuc()), HttpStatus.OK);
    }

    @GetMapping("/emails/{id}")
    public ResponseEntity<?> emails(@CurrentUser UserPrincipal user, @PathVariable Long id) {
        return new ResponseEntity<Object>(emailService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/emails")
    public ResponseEntity<?> guardar(@RequestBody EmailCompanyNotifyEntity emailCompanyNotifyEntity, @CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(emailService.save(emailCompanyNotifyEntity, false, user), HttpStatus.OK);
    }

    @PutMapping("/emails")
    public ResponseEntity<?> editar(@RequestBody EmailCompanyNotifyEntity emailCompanyNotifyEntity, @CurrentUser UserPrincipal user) {
        return new ResponseEntity<Object>(emailService.save(emailCompanyNotifyEntity, true, user), HttpStatus.OK);
    }

    @DeleteMapping("/emails/{id}")
    public void borrar(@PathVariable Long id, @CurrentUser UserPrincipal user) {
        emailService.delete(id, user.getUsername());
    }

}
