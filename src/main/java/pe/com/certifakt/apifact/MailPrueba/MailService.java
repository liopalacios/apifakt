package pe.com.certifakt.apifact.MailPrueba;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import freemarker.template.Configuration;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface MailService {

     MailResponse sendEmail(MailRequest request, Map<String, Object> model);

}
