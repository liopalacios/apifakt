package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.AditionalFieldEntity;

public interface AditionalFieldRepository extends JpaRepository<AditionalFieldEntity, Long> {
	
	@Modifying
	@Query("delete from AditionalFieldEntity p where p.id = :id")
	public void deleteAditionakField(
            @Param("id") Long id);


}
