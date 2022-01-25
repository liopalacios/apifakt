package pe.com.certifakt.apifact.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the details_payment_voucher database table.
 *
 */
@Entity
@Table(name="payment_cuotas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CuotasPaymentVoucherEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="payment_cuotas_id_cuotas_seq", sequenceName = "payment_cuotas_id_cuotas_seq", allocationSize=1 )
	@GeneratedValue(strategy= GenerationType.AUTO, generator="payment_cuotas_id_cuotas_seq")
	@Column(name="id_cuotas")
	private Long id;

	@Column(name="numero", nullable=false)
	private Integer numero;

	@Column(name="monto", precision=35, scale=20)
	private BigDecimal monto;

	@Column(name="fecha")
	private String fecha;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_payment_voucher")
	private PaymentVoucherEntity paymentVoucher;

	public void setPaymentVoucher(PaymentVoucherEntity paymentVoucher) {
		this.paymentVoucher = paymentVoucher;
	}




}