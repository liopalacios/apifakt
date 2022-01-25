package pe.com.certifakt.apifact.service;


import pe.com.certifakt.apifact.model.ClientEntity;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.util.List;
import java.util.Map;

public interface ClientService {

    List<ClientEntity> findAllByCompanyId(String ruc);

    List<ClientEntity> findByCompanyIdAndDocumento(String ruc, String tipo, String numero);

    ClientEntity saveClient(ClientEntity clientEntity, Boolean isEdit, UserPrincipal userResponse) throws Exception;

    void deleteClient(Long id, UserPrincipal userResponse);

    Map<String, Object> findByCompanyId(int pagenumber, int perpage, UserPrincipal user, String filter);

    Map<String, Object> valiteAll(List<ClientEntity> clients);

    void saveAll(List<ClientEntity> clients, UserPrincipal user);
}
