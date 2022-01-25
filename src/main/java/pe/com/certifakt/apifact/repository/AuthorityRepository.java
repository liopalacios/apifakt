package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.Authority;
import pe.com.certifakt.apifact.model.AuthorityName;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByName(AuthorityName authorityName);

}
