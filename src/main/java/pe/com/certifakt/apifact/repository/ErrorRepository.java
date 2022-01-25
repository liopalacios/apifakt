package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.ErrorEntity;
import pe.com.certifakt.apifact.util.ConstantesParameter;

import java.util.List;

public interface ErrorRepository extends JpaRepository<ErrorEntity, Integer> {

    ErrorEntity findFirst1ByCodeAndDocument(String code, String document);

    @Query("select p FROM ErrorEntity p "
            + "where p.code = :code "
            + "and   p.type = '" + ConstantesParameter.ESTADO_SUNAT_NO_ENVIADO + "'"
    )
    public List<ErrorEntity> getListCatalogoPendientes(
            @Param("code") String code);


    @Query("select p FROM ErrorEntity p "
            + "where p.code = :code "
            + "and p.code not in ('" + ConstantesParameter.CODIGO_COMPROBANTE_FUE_REGISTRADO + "') "
            + "and   p.type not in ('" + ConstantesParameter.ESTADO_SUNAT_NO_ENVIADO + "') "
    )
    public List<ErrorEntity> getListCatalogoErrores(
            @Param("code") String code);

}


