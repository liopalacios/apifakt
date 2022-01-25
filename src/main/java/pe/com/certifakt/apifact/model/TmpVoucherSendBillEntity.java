package pe.com.certifakt.apifact.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="tmp_voucher_send_bill")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmpVoucherSendBillEntity  implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7940208924518997780L;

	@Id
	@SequenceGenerator(name="tmp_send_bill_seq", sequenceName = "tmp_send_bill_seq" , allocationSize=1)
	@GeneratedValue(strategy= GenerationType.AUTO, generator="tmp_send_bill_seq")
	@Column(name="id_tmp_send_bill")
	private Long idTmpSendBill;

	@Column(name="id_payment_voucher", nullable=false)
	private Long idPaymentVoucher;
	@Column(name="estado", length=1)
	private String estado;
	@Column(name="name_document", nullable=false, length=150)
	private String nombreDocumento;
	@Column(name="uuid_saved", nullable=false, length=210)
	private String uuidSaved;
	@Column(name="tipo_comprobante", nullable=false, length=2)
	private String tipoComprobante;

}
