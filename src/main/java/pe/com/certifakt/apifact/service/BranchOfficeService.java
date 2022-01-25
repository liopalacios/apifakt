package pe.com.certifakt.apifact.service;


import pe.com.certifakt.apifact.model.BranchOfficeEntity;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.util.List;

public interface BranchOfficeService {

    List<BranchOfficeEntity> findAllByCompanyId(String ruc);

    BranchOfficeEntity findById(Integer id);

    BranchOfficeEntity saveBranchOffice(BranchOfficeEntity branchOfficeEntity, Boolean isEdit, UserPrincipal user);

    void deleteBranchOffice(Integer id, String user);
}
