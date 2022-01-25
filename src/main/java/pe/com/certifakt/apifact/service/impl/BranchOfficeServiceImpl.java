package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.model.BranchOfficeEntity;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.User;
import pe.com.certifakt.apifact.repository.BranchOfficeRepository;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.UserRepository;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.BranchOfficeService;

import java.util.List;

@Service
@AllArgsConstructor
public class BranchOfficeServiceImpl implements BranchOfficeService {

    private final BranchOfficeRepository branchOfficeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BranchOfficeEntity> findAllByCompanyId(String ruc) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);

        List<BranchOfficeEntity> branchOfficeEntities = branchOfficeRepository.findAllByCompanyIdAndEstadoIsTrue(companyEntity.getId());

        branchOfficeEntities.forEach(bo -> {
            bo.getSeries();
            bo.getUsuarios();
        });

        return branchOfficeEntities;
    }

    @Override
    public BranchOfficeEntity findById(Integer id) {
        BranchOfficeEntity oficina = branchOfficeRepository.findById(id).get();
        return oficina;
    }

    @Override
    @Transactional
    public BranchOfficeEntity saveBranchOffice(BranchOfficeEntity branchOffice, Boolean isEdit, UserPrincipal user) {

        CompanyEntity companyEntity = companyRepository.findByRuc(user.getRuc());

        if (isEdit) {
            BranchOfficeEntity branchOfficeEntity = branchOfficeRepository.findById(branchOffice.getId()).get();
            branchOfficeEntity.setDepartamento(branchOffice.getDepartamento());
            branchOfficeEntity.setProvincia(branchOffice.getProvincia());
            branchOfficeEntity.setDireccion(branchOffice.getDireccion());
            branchOfficeEntity.setDistrito(branchOffice.getDistrito());
            branchOfficeEntity.setNombreCorto(branchOffice.getNombreCorto());
            branchOfficeEntity.setUpdatedBy(user.getUsername());
            branchOfficeEntity.setCompany(companyEntity);
            return branchOfficeRepository.save(branchOfficeEntity);
        } else {
            branchOffice.setCreatedBy(user.getUsername());
            branchOffice.setEstado(true);
            branchOffice.setCompany(companyEntity);
            return branchOfficeRepository.save(branchOffice);
        }

    }

    @Override
    public void deleteBranchOffice(Integer id, String user) {
        BranchOfficeEntity branchOfficeEntity = branchOfficeRepository.findById(id).get();
        branchOfficeEntity.setEstado(false);
        branchOfficeEntity.setUpdatedBy(user);
        branchOfficeRepository.save(branchOfficeEntity);

        for (User u : branchOfficeEntity.getUsuarios()) {
            u.setOficina(null);
            ;
            userRepository.save(u);
        }
    }

}
