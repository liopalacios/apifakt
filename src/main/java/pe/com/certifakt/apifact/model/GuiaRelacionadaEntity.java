package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "guia_payment_voucher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuiaRelacionadaEntity {

    @Id
    @SequenceGenerator(name = "guia_payment_voucher_seq", sequenceName = "guia_payment_voucher_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "guia_payment_voucher_seq")
    @Column(name = "id_guia_payment")
    private Long idGuiaPayment;

    private String codigoTipoGuia;
    private String serieNumeroGuia;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_payment_voucher")
    private PaymentVoucherEntity paymentVoucher;
}
