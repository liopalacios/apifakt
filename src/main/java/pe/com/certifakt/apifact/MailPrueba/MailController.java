package pe.com.certifakt.apifact.MailPrueba;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pe.com.certifakt.apifact.service.EmailService;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
public class MailController {

    @Autowired
    private MailServiceImlp service;

    @PostMapping("/sendEmail")
    public MailResponse sendMail(@RequestBody MailRequest request)
    {
        Map<String,Object> model= new HashMap<>();

        model.put("name",request.getName());
        model.put("location","Lima,Peru");

        return service.sendEmail(request,model);

    }
}
