package pe.com.certifakt.apifact.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.com.certifakt.apifact.dto.PaymentMercadoPagoDto;
import pe.com.certifakt.apifact.dto.PaypalWebhook;
import pe.com.certifakt.apifact.repository.PaymentPaypalRepository;
import pe.com.certifakt.apifact.service.PaymentPaypalService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Slf4j
@AllArgsConstructor
public class PaymentsController {
    private final PaymentPaypalService paypalService;

    private static final String USER_AGENT = "ME IPN Responder";
    private static Logger LOG = LoggerFactory.getLogger(PaymentsController.class);
    @SuppressWarnings("unused")
    private static final String urlPaypalSandbox1 = "https://ipnpb.sandbox.paypal.com/cgi-bin/webscr";
    private static final String urlPaypalSandbox2 =  "https://www.sandbox.paypal.com/cgi-bin/webscr";
    @SuppressWarnings("unused")
    private static final String urlPaypalLive1 = "https://ipnpb.paypal.com/cgi-bin/webscr";
    private static final String urlPaypalLive2 = "https://www.paypal.com/cgi-bin/webscr";
    private static final boolean sandboxmode = true;

    @PostMapping("/mercadopago")
    public void paymentmercadopago(@RequestParam(value = "topic", required = true) String topic,
                                   @RequestParam(value = "id", required = true) Long id) {

        //paypalService.saveMercadoPago(topic, id);
    }
    @PostMapping("/mpwebhooksold")
    public void paymentwebhooks(@RequestBody PaymentMercadoPagoDto dto) throws IOException {


    }

    @ExceptionHandler
    public String handleInvalide(InvalidFieldException e){
        return e.getMessage();
    }

    ///// PAYPAL
    @PostMapping("/paypalwebhooksold")
    public void paypalwebhooks(@RequestBody PaypalWebhook httpRequest) throws IOException {

    }
    @PostMapping("/listenpaypalold")
    public void listenpaypal(@RequestBody String httpRequest) throws IOException {
        System.out.println("MENSAJE RECIBIDO PAYPAL NOTIFICACION");
        System.out.println(httpRequest);
        //paypalService.savePaypal(httpRequest.getParameterMap());

    }
    @RequestMapping(value="/listenpaypaldosold", method = RequestMethod.POST)
    public void listenpaypaldos(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("MENSAJE RECIBIDO PAYPAL NOTIFICACION SEGUNDO");
        String reqUri = "";
        if (request != null) {
            reqUri = request.getRequestURI();
        }

        LOG.info("[ uri : {} ] - IPN Callback wird aufgerufen", reqUri);
        if(LOG.isDebugEnabled()) {
            logRequestHeaders(request);
        }
        // write an ipn flag to bestellung or do some other clever things
        LOG.debug("Invoice: "+request.getParameter("invoice"));

        try {
            String responseData = buildResponseData(request.getParameterNames(), request.getParameterMap());

            // TODO Identifizieren der Bestellung an Hand von Informationen aus dem IPN
            String paypalurl = urlPaypalSandbox2;
            if(!sandboxmode) {
                paypalurl = urlPaypalLive2;
            }
            //sendIpnMessageToPaypal2(paypalurl, responseData);
            //sendIpnMessageToPaypal2("http://localhost:1902/xxx", buffer.toString());
            response.setStatus(200);
        } catch (Exception e) {
            response.setStatus(500);
            LOG.error(e.getMessage());
        }

    }
    String buildResponseData(Enumeration<String> n, Map<String, String[]> map) throws UnsupportedEncodingException {
        StringBuffer buffer = new StringBuffer("cmd=_notify-validate");
        while (n.hasMoreElements()) {
            buffer.append("&");
            String s = (String) n.nextElement();
            buffer.append(s);
            buffer.append("=");
            buffer.append(URLEncoder.encode(map.get(s)[0], "UTF-8"));
        }
        return buffer.toString();
    }
    private void logRequestHeaders(HttpServletRequest request) {
        Enumeration<String> h = request.getHeaderNames();
        while (h.hasMoreElements()) {
            String s = (String) h.nextElement();
            LOG.debug("Header: "+s+" - "+request.getHeader(s));
        }
    }

    private void sendIpnMessageToPaypal2(String url, String ipnReturnMessage) throws Exception {
        // TODO do this in a new thread
        // Don't write live customer data to logs
        if(sandboxmode) {
            LOG.debug("IPN: "+ipnReturnMessage);
        }
        LOG.info("Send IPN Message 'verified' to Paypal: "+url);

        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        OutputStream os = conn.getOutputStream();
        os.write(ipnReturnMessage.getBytes());
        conn.connect();

        InputStream in = conn.getInputStream();
        String ins = IOUtils.toString(in, "UTF-8");
        String m = conn.getResponseMessage();
        LOG.debug("Response Code : "  + conn.getResponseCode()+" "+m+" - "+ins);
        if (ins.equalsIgnoreCase("VERIFIED")) {
            LOG.info("IPN Message verified by Paypal successfully");
        } else {
            throw new Exception("IPN Message not verified by Paypal: "+ins);
        }
    }
}