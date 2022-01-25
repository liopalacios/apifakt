package pe.com.certifakt.apifact.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "webhooks_paypal")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebhooksPaypanEntity {
    @Id
    @SequenceGenerator(name = "payment_paypal_id_payment_paypal_seq", sequenceName = "payment_paypal_id_payment_paypal_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "payment_paypal_id_payment_paypal_seq")
    @Column(name = "id_webhooks_paypal")
    private Long id;
    @Column(name = "id_webhooks")
    private String idweb;

    private String tipo;
    private String createweb;

    private String summary;
    private boolean finalcapture;
    private String resourceid;
    private String resourcestatus;
    private String amountcurrency;
    private String amountvalue;

}
