package pe.com.certifakt.apifact.repository;

import org.springframework.data.repository.CrudRepository;
import pe.com.certifakt.apifact.model.DocumentDownloadFileEntity;
import pe.com.certifakt.apifact.model.RegisterDownloadUploadEntity;

public interface RegisterDownloadUploadRepository extends CrudRepository<RegisterDownloadUploadEntity, Long> {


}
