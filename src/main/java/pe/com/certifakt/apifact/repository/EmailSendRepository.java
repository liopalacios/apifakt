package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.DowloadExcelEntity;
import pe.com.certifakt.apifact.model.EmailSendEntity;

public interface EmailSendRepository extends JpaRepository<EmailSendEntity, Long> {

}
