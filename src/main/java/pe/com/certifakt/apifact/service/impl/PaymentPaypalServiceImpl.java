package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.dto.PaymentMercadoPagoDto;
import pe.com.certifakt.apifact.dto.PaymentPaypalDto;
import pe.com.certifakt.apifact.dto.PaypalWebhook;
import pe.com.certifakt.apifact.model.MerkdopagoNotifyEntity;
import pe.com.certifakt.apifact.model.PaymentPaypalEntity;
import pe.com.certifakt.apifact.model.WebhooksPaypanEntity;
import pe.com.certifakt.apifact.repository.ClientRepository;
import pe.com.certifakt.apifact.repository.MerkdoPagoRepository;
import pe.com.certifakt.apifact.repository.PaymentPaypalRepository;
import pe.com.certifakt.apifact.repository.WebhooksPaypalRepository;
import pe.com.certifakt.apifact.service.PaymentPaypalService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Service
@AllArgsConstructor
public class PaymentPaypalServiceImpl implements PaymentPaypalService {

    private final PaymentPaypalRepository paypalRepository;

    private final WebhooksPaypalRepository webhooksPaypalRepository;

    private final MerkdoPagoRepository merkdoPagoRepository;

    private static Logger LOG = LoggerFactory.getLogger(PaymentPaypalServiceImpl.class);


    @Override
    public PaymentPaypalEntity generationPaymentPaypal(String ruc, PaymentPaypalDto paypal) {

        PaymentPaypalEntity entity = paypalRepository.save(PaymentPaypalEntity.builder()
                                        .ruc(ruc)
                                        .fecha(paypal.getFecha())
                                        .hora(paypal.getHora())
                                        .cliente(paypal.getCliente())
                                        .clienteemail(paypal.getClienteemail())
                                        .clienteid(paypal.getClienteid())
                                        .clientedireccion(paypal.getClientedireccion())
                                        .clientepais(paypal.getClientepais())
                                        .clientepaiscod(paypal.getClientepaiscod())
                                        .ciudad(paypal.getCiudad())
                                        .calle(paypal.getCalle())
                                        .vendedor(paypal.getVendedor())
                                        .receptoremail(paypal.getReceptoremail())
                                        .receptorid(paypal.getReceptorid())
                                        .productname(paypal.getProductname())
                                        .productid(paypal.getProductid())
                                        .cantidad(paypal.getCantidad())
                                        .envio(paypal.getEnvio())
                                        .igv(paypal.getIgv())
                                        .moneda(paypal.getMoneda())
                                        .cuota(paypal.getCuota())
                                        .bruto(paypal.getBruto())
                                        .bruto1(paypal.getBruto1())
                                        .transaccion(paypal.getTransaccion())
                                        .comprobante(paypal.getComprobante())
                                        .verify(paypal.getVerify())
                                        .datajson(paypal.getDatajson()).build());
        return entity;
    }

    @Override
    public void savePaypalWebhook(PaypalWebhook httpRequest) {
        try {
            webhooksPaypalRepository.save(WebhooksPaypanEntity.builder()
                    .idweb(httpRequest.getId())
                    .tipo(httpRequest.getEvent_type())
                    .createweb(httpRequest.getCreate_time())
                    .resourceid(httpRequest.getResource().getId())
                    .summary(httpRequest.getSummary())
                    .finalcapture(httpRequest.getResource().isFinal_capture())
                    .resourcestatus(httpRequest.getResource().getStatus())
                    .amountcurrency(httpRequest.getResource().getAmount().getCurrency_code())
                    .amountvalue(httpRequest.getResource().getAmount().getValue())
                    .build());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void savePaypal(Map<String, String[]> parameterMap) throws IOException {
        SimpleDateFormat  format = new SimpleDateFormat("YYYY-MM-dd");
        SimpleDateFormat formathora = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        StringWriter writer = new StringWriter();
        JSONValue.writeJSONString(parameterMap,writer);
        String s = writer.toString();
        System.out.println("JSON DE PAYPAL");
        System.out.println(s);
        Timestamp timestamp = new Timestamp(date.getTime());
        try {
            paypalRepository.save(PaymentPaypalEntity.builder()
                    .fecha(format.format(date))
                    .hora(formathora.format(date))
                    .cliente(parameterMap.get("first_name")[0] + " " + parameterMap.get("last_name")[0])
                    .clienteemail(parameterMap.get("payer_email")[0])
                    .clienteid(parameterMap.get("payer_id")[0])
                    .clientedireccion(parameterMap.get("address_name")[0])
                    .clientepais(parameterMap.get("address_country")[0])
                    .clientepaiscod(parameterMap.get("address_country_code")[0])
                    .ciudad(parameterMap.get("address_city")[0])
                    .calle(parameterMap.get("address_street")[0])
                    .vendedor(parameterMap.get("business")[0])
                    .receptoremail(parameterMap.get("receiver_email")[0])
                    .receptorid(parameterMap.get("receiver_id")[0])
                    .productname(parameterMap.get("item_name")[0])
                    .productid(parameterMap.get("item_number")[0])
                    .cantidad(Integer.parseInt(parameterMap.get("quantity")[0]))
                    .envio(Double.parseDouble(parameterMap.get("shipping")[0]))
                    .igv(Double.parseDouble(parameterMap.get("tax")[0]))
                    .moneda(parameterMap.get("mc_currency")[0])
                    .cuota(Double.parseDouble(parameterMap.get("mc_fee")[0]))
                    .bruto(Double.parseDouble(parameterMap.get("mc_gross")[0]))
                    .bruto1(Double.parseDouble(parameterMap.get("mc_gross_1")[0]))
                    .transaccion(parameterMap.get("txn_id")[0])
                    .comprobante(parameterMap.get("invoice")[0])
                    .verify(parameterMap.get("verify_sign")[0])
                    .created(timestamp)
                    .datajson(s)
                    .build());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void saveMercadoPagoWebhooks(PaymentMercadoPagoDto dto) {
        merkdoPagoRepository.save(MerkdopagoNotifyEntity.builder()
                .idmerkdopago(dto.getId())
                .registro(new Date())
                .action(dto.getAction())
                .creado(dto.getDate_created())
                .produccion(dto.getLive_mode())
                .usuario(dto.getUser_id())
                .action(dto.getAction())
                .dataid(dto.getData()==null?"":dto.getData().getId())
                .topic(dto.getType()).build());
    }



    @Override
    public void saveMercadoPago(String topic, Long id) {
        merkdoPagoRepository.save(MerkdopagoNotifyEntity.builder()
                .idmerkdopago(id)
                .registro(new Date())
                .topic(topic).build());
    }

    private void sendIpnMessageToPaypal(String url, String ipnReturnMessage) throws Exception {
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
