package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.DetailOtherCpeEntity;

public interface DetailOtherCpeRepository extends JpaRepository<DetailOtherCpeEntity, Long> {

    @Modifying
    @Query("delete from DetailOtherCpeEntity oc where oc.idDetailOtherCpe = :idDetailOtherCpe")
    public void deleteDetailsOtherCpe(
            @Param("idDetailOtherCpe") Long idDetailOtherCpe);

}
