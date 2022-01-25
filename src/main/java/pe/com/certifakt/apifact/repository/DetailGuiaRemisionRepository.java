package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.DetailGuiaRemisionEntity;

public interface DetailGuiaRemisionRepository extends JpaRepository<DetailGuiaRemisionEntity, Long> {

    @Modifying
    @Query("delete from DetailGuiaRemisionEntity g where g.idDetailGuiaRemision = :idDetailGuiaRemision")
    public void deleteDetailsGuiaRemision(
            @Param("idDetailGuiaRemision") Long idDetailGuiaRemision);
}
