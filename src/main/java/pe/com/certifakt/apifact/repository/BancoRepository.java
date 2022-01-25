package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.BancoEntity;

public interface BancoRepository extends JpaRepository<BancoEntity, Integer> {

    BancoEntity findByCode(String code);

}
