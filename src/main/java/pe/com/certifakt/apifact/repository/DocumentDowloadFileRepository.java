package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.DocumentDownloadFileEntity;

public interface DocumentDowloadFileRepository extends JpaRepository<DocumentDownloadFileEntity,Long> {

}
