package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.BranchOfficeEntity;

import java.util.List;
import java.util.Optional;

public interface BranchOfficeRepository extends JpaRepository<BranchOfficeEntity, Integer> {

    List<BranchOfficeEntity> findAllByCompanyIdAndEstadoIsTrue(Integer id);

    List<BranchOfficeEntity> findAllByCompanyId(Integer id);
}
