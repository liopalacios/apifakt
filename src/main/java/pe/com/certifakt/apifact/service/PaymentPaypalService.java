package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.dto.PaymentMercadoPagoDto;
import pe.com.certifakt.apifact.dto.PaymentPaypalDto;
import pe.com.certifakt.apifact.dto.PaypalWebhook;
import pe.com.certifakt.apifact.model.PaymentPaypalEntity;

import java.io.IOException;
import java.util.Map;

public interface PaymentPaypalService {

    PaymentPaypalEntity generationPaymentPaypal(String ruc, PaymentPaypalDto paypal);

    void saveMercadoPago(String topic, Long id);

    void savePaypal(Map<String, String[]> parameterMap) throws IOException;

    void saveMercadoPagoWebhooks(PaymentMercadoPagoDto dto);

    void savePaypalWebhook(PaypalWebhook httpRequest);
}
