package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMercadoPagoDto implements Serializable {

    private String issuer_id;
    private String money_release_date;
    private int shipping_amount;
    private String statement_descriptor;

    private Float transaction_amount_refunded; // Monto total reembolsado en este pago
    private Float coupon_amount;  // 0
    private String type;

    private Long application_id;
    private Long user_id;
    private int version;
    private String api_version;
    private String action;
    private IdDto data;

    private String date_of_expiration;
    private String payment_method_id;  //deb visa
    private String payment_type_id;  // deb card
    private String status_detail;  // refunded
    private String sponsor_id;
    private String money_release_schema;  // null
    private String counter_currency;   // null

    //private  Float application_fee;
    //private AditionalMercadoPagoDto additional_info;


    private String operation_type;  // regular payment
    private String date_approved;
    private PayerMercadoPago payer;
    private Long id;
    private MercadoPagoOrderDTO order;
    private Boolean live_mode; // true
    private MercadoPagoCardDTO card;  // CLIENTE RECEPTOR DATOS
    private String status;  //
    private Float transaction_amount; // Costo del producto. (Obligatorio)
    private String description;
    private String authorization_code;   // 116584
    private BigDecimal collector_id;   // 553996772
    private String date_last_updated;
    private String date_created;
    private String currency_id;  // MONEDA
}
