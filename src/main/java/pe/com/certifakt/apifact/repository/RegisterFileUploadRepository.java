package pe.com.certifakt.apifact.repository;

import org.springframework.data.repository.CrudRepository;
import pe.com.certifakt.apifact.model.RegisterFileUploadEntity;

public interface RegisterFileUploadRepository extends CrudRepository<RegisterFileUploadEntity, Long> {

}
