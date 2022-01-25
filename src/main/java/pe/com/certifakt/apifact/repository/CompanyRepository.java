package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.dto.inter.OseInterDto;
import pe.com.certifakt.apifact.dto.inter.PaymentVoucherInterDto;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.DowloadExcelEntity;
import pe.com.certifakt.apifact.model.OsesEntity;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesSunat;

import java.util.Date;
import java.util.List;

public interface CompanyRepository extends CrudRepository<CompanyEntity, Integer> {

    CompanyEntity findByRuc(String ruc);

    @Query("SELECT o FROM CompanyEntity c "
            + " inner join c.ose o "
            + " where c.ruc = :rucCompany ")
    OsesEntity findOseByRuc(@Param("rucCompany") String rucCompany);

    @Query(value ="select o.oses_id as id, o.url_facturas as urlfacturas, o.url_guias as urlguias " +
            "from company c inner join oses o on o.oses_id = c.ose_id  "
            + " where c.ruc = ?1 ", nativeQuery = true)
    OseInterDto findOseByRucInter(String rucCompany);

    @Query(value ="SELECT c.ruc FROM company c WHERE c.estado = "
            + "'" + ConstantesParameter.REGISTRO_ACTIVO + "'",nativeQuery = true)
    List<String> getCompaniesForSummaryDocuments();

    @Query(value ="SELECT c.estado FROM company c "
            + "where c.ruc = ?1 ",nativeQuery = true)
    String getStateCompanyByRuc(String rucCompany);



    @Query("SELECT c FROM CompanyEntity c "
            + "where c.fechaBaja <= :format and c.fechaBaja is not null and c.estado = 'A'  ")
    List<CompanyEntity> getAllLimitCompany(@Param("format") Date format);
}
