package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.AnticipoEntity;

public interface AnticipoRepository extends JpaRepository<AnticipoEntity, Long> {

    @Modifying
    @Query("delete from AnticipoEntity p where p.idAnticipoPayment = :idAnticipoPayment")
    public void deleteAnticipo(
            @Param("idAnticipoPayment") Long idAnticipoPayment);


}
