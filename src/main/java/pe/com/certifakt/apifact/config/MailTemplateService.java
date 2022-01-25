package pe.com.certifakt.apifact.config;

import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

@Component
@AllArgsConstructor
public class MailTemplateService {

    private final Configuration freemarkerConfiguration;

    public String getFreeMarkerTemplateContent(Map<String, Object> model, String template) {
        StringBuffer content = new StringBuffer();
        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfiguration.getTemplate(template), model));
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
