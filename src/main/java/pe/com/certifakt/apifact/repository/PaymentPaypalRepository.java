package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.com.certifakt.apifact.model.MerkdopagoNotifyEntity;
import pe.com.certifakt.apifact.model.PaymentPaypalEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentPaypalRepository extends CrudRepository<PaymentPaypalEntity, Long> {

    @Query("select p from PaymentPaypalEntity p where created >= :date and generated is null")
    List<PaymentPaypalEntity> findAllByCreated(Date date);


    Optional<PaymentPaypalEntity> findByTransaccion(String transaction_id);
}
