package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="anticipo_payment_voucher")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnticipoEntity {

	@Id
	@SequenceGenerator(name="anticipo_payment_voucher_seq", sequenceName = "anticipo_payment_voucher_seq", allocationSize=1 )
	@GeneratedValue(strategy= GenerationType.AUTO, generator="anticipo_payment_voucher_seq")
	@Column(name="id_anticipo_payment")
	private Long idAnticipoPayment;

	@Column(name="serie_anticipo", length=4, nullable=false)
	private String serieAnticipo;
	@Column(name="numero_anticipo", nullable=false)
	private Integer numeroAnticipo;
	@Column(name="tipo_doc_anticipo", length=2, nullable=false)
	private String tipoDocumentoAnticipo;
	@Column(name="monto_anticipo", precision=12, scale=12, nullable=false)
	private BigDecimal montoAnticipo;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_payment_voucher")
	private PaymentVoucherEntity paymentVoucher;
}
