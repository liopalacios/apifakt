package pe.com.certifakt.apifact.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.certifakt.apifact.model.DowloadExcelEntity;
import pe.com.certifakt.apifact.model.EmailSendEntity;

import java.util.List;

public interface DowloadExcelRepository extends JpaRepository<DowloadExcelEntity,Long>{

    DowloadExcelEntity findByIdentificadorAndTipoDocumentoAndSerieAndEstadoDescargaAndFechaSolicitud(String identificador,String tipoDocumento, String serie, String estadoDescarga, String fechaSolicitud);

    @Query(value = "SELECT d FROM DowloadExcelEntity d where d.codCompany = ?1 ORDER BY d.fechaSolicitud desc, idExcelDocument desc ")
    Page<DowloadExcelEntity> findAllExcelsForPagesOrderByFecha_solicitudDesc(Integer codCompany, Pageable pageable);

    @Query(value = "SELECT d FROM DowloadExcelEntity d")
    List<DowloadExcelEntity> obtenerExcels();

    DowloadExcelEntity findByIdExcelDocument(Long id);


}