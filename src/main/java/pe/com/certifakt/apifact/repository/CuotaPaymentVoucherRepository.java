package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.AditionalFieldEntity;
import pe.com.certifakt.apifact.model.CuotasPaymentVoucherEntity;

public interface CuotaPaymentVoucherRepository extends JpaRepository<CuotasPaymentVoucherEntity, Long> {
	
	@Modifying
	@Query("delete from CuotasPaymentVoucherEntity p where p.id = :id")
	public void deleteCuotaPayment(
            @Param("id") Long id);


}
