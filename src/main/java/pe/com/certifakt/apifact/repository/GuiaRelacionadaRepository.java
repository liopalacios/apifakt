package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.GuiaRelacionadaEntity;

public interface GuiaRelacionadaRepository extends JpaRepository<GuiaRelacionadaEntity, Long> {

    @Modifying
    @Query("delete from GuiaRelacionadaEntity p where p.idGuiaPayment = :idGuiaPayment")
    public void deleteGuiaRelacionada(
            @Param("idGuiaPayment") Long idGuiaPayment);


}
