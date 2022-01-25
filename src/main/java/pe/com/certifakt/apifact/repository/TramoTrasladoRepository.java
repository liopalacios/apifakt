package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.TramoTrasladoEntity;

public interface TramoTrasladoRepository extends JpaRepository<TramoTrasladoEntity, Long> {

    @Modifying
    @Query("delete from TramoTrasladoEntity t where t.idTramoGuiaRemision = :idTramoGuiaRemision")
    public void deleteTramoGuiaRemision(
            @Param("idTramoGuiaRemision") Long idTramoGuiaRemision);
}
