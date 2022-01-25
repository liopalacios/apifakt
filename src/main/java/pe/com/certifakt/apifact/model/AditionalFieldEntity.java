package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "aditional_field_payment_voucher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AditionalFieldEntity {

    @Id
    @SequenceGenerator(name = "aditional_field_payment_voucher_seq", sequenceName = "aditional_field_payment_voucher_seq", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "aditional_field_payment_voucher_seq")
    private Long id;

    @Column
    private String nombreCampo;

    private String valorCampo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_payment_voucher")
    @JsonIgnore
    private PaymentVoucherEntity paymentVoucher;


    @ManyToOne(cascade = CascadeType.ALL)
    private TypeFieldEntity typeField;
    
}
