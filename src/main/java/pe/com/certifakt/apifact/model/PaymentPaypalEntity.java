package pe.com.certifakt.apifact.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "payment_paypal")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPaypalEntity {
    @Id
    @SequenceGenerator(name = "payment_paypal_id_payment_paypal_seq", sequenceName = "payment_paypal_id_payment_paypal_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "payment_paypal_id_payment_paypal_seq")
    @Column(name = "id_payment_paypal")
    private Long id;
    private String transaccion;
    @Column(name = "fecha_emision")
    private String fecha;
    @Column(name = "hora_emision")
    private String hora;
    @Column(name = "transaction_status")
    private String transactionStatus;
    private String invoice;
    private String moneda;
    @Column(name = "value_amount")
    private String valueAmount;
    private String ciudad;
    @Column(name = "receptor_email")
    private String receptoremail;
    @Column(name = "cliente_email")
    private String clienteemail;
    private String cliente;
    @Column(name = "product_name")
    private String productname;

    @Column(name = "ruc_emisor")
    private String ruc;

    private Double bruto;





    @Column(name = "cliente_id")
    private String clienteid;
    @Column(name = "cliente_direccion")
    private String clientedireccion;
    @Column(name = "cliente_pais")
    private String clientepais;
    @Column(name = "cliente_pais_cod")
    private String clientepaiscod;

    private String calle;
    private String vendedor;

    @Column(name = "receptor_id")
    private String receptorid;

    @Column(name = "product_id")
    private String productid;
    private int cantidad;
    private Double envio;
    private Double igv;

    private Double cuota;

    private Double bruto1;

    private String comprobante;
    private String verify;
    private String datajson;
    private Timestamp created;
    private Timestamp generated;
}
