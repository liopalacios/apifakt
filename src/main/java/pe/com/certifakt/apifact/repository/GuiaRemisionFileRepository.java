package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pe.com.certifakt.apifact.dto.inter.RegisterFileUploadInterDto;
import pe.com.certifakt.apifact.enums.EstadoArchivoEnum;
import pe.com.certifakt.apifact.enums.TipoArchivoEnum;
import pe.com.certifakt.apifact.model.GuiaRemisionFileEntity;

public interface GuiaRemisionFileRepository extends CrudRepository<GuiaRemisionFileEntity, Long> {

    GuiaRemisionFileEntity findFirst1ByGuiaRemision_IdGuiaRemisionAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(Long idPayment, TipoArchivoEnum tipoArchivo, EstadoArchivoEnum estadoArchivo);

    @Query(value = "select gr.id_guia_remision as id, u.is_old as isOld, u.bucket, u.nombre_generado as nombreGenerado, " +
            "u.ruc_company as rucCompany, u.uuid, u.extension, pvf.tipo_archivo as tipo from register_file_upload u " +
            "inner join guia_file pvf on pvf.id_register_file_send = u.id_register_file_send \n" +
            "inner join guia_remision gr on gr.id_guia_remision = pvf.id_guia_remision  \n" +
            "where gr.id_guia_remision = ?1 and pvf.tipo_archivo = ?2 and pvf.estado_archivo = ?3   \n" +
            "order by u.id_register_file_send desc \n" +
            "limit 1",nativeQuery = true)
    RegisterFileUploadInterDto findFirst1ByGuiaRemisionIdGuiaRemisionAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc(Long idVoucher, String name, String name1);
}
