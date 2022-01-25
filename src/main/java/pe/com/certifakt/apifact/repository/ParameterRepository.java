package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.com.certifakt.apifact.model.ParameterEntity;

@Repository
public interface ParameterRepository extends JpaRepository<ParameterEntity, Integer> {
    ParameterEntity findByName(String name);
}
