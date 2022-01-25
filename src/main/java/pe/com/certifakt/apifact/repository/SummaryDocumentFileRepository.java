package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.SummaryFileEntity;

public interface SummaryDocumentFileRepository extends JpaRepository<SummaryFileEntity, Long> {

}
