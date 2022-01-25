package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.EmailCompanyNotifyEntity;

import java.util.List;

public interface EmailCompanyNotifyRepository extends JpaRepository<EmailCompanyNotifyEntity, Long> {

    List<EmailCompanyNotifyEntity> findAllByCompany_RucAndEstadoIsTrue(String ruc);
}
