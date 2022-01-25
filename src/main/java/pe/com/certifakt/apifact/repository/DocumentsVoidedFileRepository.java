package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.VoidedFileEntity;

public interface DocumentsVoidedFileRepository extends JpaRepository<VoidedFileEntity, Long> {


}
