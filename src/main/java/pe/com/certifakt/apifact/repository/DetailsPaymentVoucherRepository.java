package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.DetailsPaymentVoucherEntity;

import java.util.List;

public interface DetailsPaymentVoucherRepository extends JpaRepository<DetailsPaymentVoucherEntity, Long> {

    @Modifying
    @Query("delete from DetailsPaymentVoucherEntity p where p.idDetailsPayment = :idDetailsPayment")
    public void deleteDetailsPaymentVoucher(
            @Param("idDetailsPayment") Long idDetailsPayment);
}
