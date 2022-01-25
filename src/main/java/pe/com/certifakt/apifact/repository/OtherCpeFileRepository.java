package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.model.OtherCpeFileEntity;

public interface OtherCpeFileRepository extends CrudRepository<OtherCpeFileEntity, Long> {

    OtherCpeFileEntity findFirst1ByOtherCpe_IdOtroCPEAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(Long idPayment, TipoArchivoEnum tipoArchivo, EstadoArchivoEnum estadoArchivo);

    @Query(value = "select oc.id_otros_cpe as id, u.is_old as isOld, u.bucket, u.nombre_generado as nombreGenerado, " +
            "u.ruc_company as rucCompany, u.uuid, u.extension, " +
            "pvf.tipo_archivo as tipo from register_file_upload u " +
            "inner join other_cpe_file pvf on pvf.id_register_file_send = u.id_register_file_send \n" +
            "inner join otros_cpe oc on oc.id_otros_cpe = pvf.id_otros_cpe  \n" +
            "where oc.id_otros_cpe = ?1 and pvf.tipo_archivo = ?2 and pvf.estado_archivo = ?3  \n" +
            "order by u.id_register_file_send desc \n" +
            "limit 1",nativeQuery = true)
    RegisterFileUploadInterDto findFirst1ByOtherCpeIdOtroCPEAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(Long idVoucher, String name, String name1);
}
