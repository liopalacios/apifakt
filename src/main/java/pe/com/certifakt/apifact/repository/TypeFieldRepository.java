package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.TypeFieldEntity;

public interface TypeFieldRepository extends JpaRepository<TypeFieldEntity, Long> {

    TypeFieldEntity findByName(String name);

}
