package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.UnitCode;

import java.util.List;

public interface UnitCodeRepository extends JpaRepository<UnitCode, Long> {

    UnitCode findByCode(String code);

    @Query("select p from UnitCode as p where p.esUsada = true and ( lower(p.code) like lower(concat('%', :codigo,'%')) or lower(p.description) like lower(concat('%', :descripcion,'%')))")
    List<UnitCode> searchUnidades(@Param("codigo") String codigo, @Param("descripcion") String descripcion);


}
