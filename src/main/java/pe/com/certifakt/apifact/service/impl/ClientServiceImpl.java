package pe.com.certifakt.apifact.service.impl;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.certifakt.apifact.bean.UserResponse;
import pe.com.certifakt.apifact.model.ClientEntity;
import pe.com.certifakt.apifact.model.CompanyEntity;
import pe.com.certifakt.apifact.model.User;
import pe.com.certifakt.apifact.repository.ClientRepository;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.UserRepository;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.ClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {


    private final ClientRepository clientRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    public List<ClientEntity> findAllByCompanyId(String ruc) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        return clientRepository.findAllByCompanyIdAndEstadoIsTrueOrderByNumeroDocumento(companyEntity.getId());
    }

    @Override
    public List<ClientEntity> findByCompanyIdAndDocumento(String ruc, String tipo, String numero) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        return clientRepository.findAllByCompanyIdAndTipoDocumentoAndNumeroDocumentoStartingWithAndEstadoIsTrue(companyEntity.getId(), tipo, numero);
    }

    @Override
    @Transactional
    public ClientEntity saveClient(ClientEntity client, Boolean isEdit, UserPrincipal userResponse) throws Exception {

        CompanyEntity companyEntity = companyRepository.findByRuc(userResponse.getRuc());
        if (client != null && client.getNumeroDocumento() != null && !client.getNumeroDocumento().isEmpty()) {
            ClientEntity result = clientRepository.findByCompanyIdAndTipoDocumentoAndNumeroDocumentoAndEstadoIsTrue
                    (companyEntity.getId(), client.getTipoDocumento(), client.getNumeroDocumento());
            if (isEdit && result != null) {
                ClientEntity clientEntity = result;
                clientEntity.setDireccionFiscal(client.getDireccionFiscal());
                clientEntity.setEmail(client.getEmail());
                clientEntity.setNombreComercial(client.getNombreComercial());
                clientEntity.setRazonSocial(client.getRazonSocial());
                clientEntity.setNumeroDocumento(client.getNumeroDocumento());
                clientEntity.setTipoDocumento(client.getTipoDocumento());
                clientEntity.setTelefonoFijo(client.getTelefonoFijo());
                clientEntity.setTelefonoMovil(client.getTelefonoMovil());
                clientEntity.setCompany(companyEntity);
                clientEntity.setUpdatedBy(userResponse.getUsername());
                clientEntity.setEstado(client.isEstado());
                clientEntity.setCondicionPago(client.getCondicionPago());
                return clientRepository.save(clientEntity);
            } else {
                List<ClientEntity> listResult = clientRepository.findAllByCompanyIdAndTipoDocumentoAndNumeroDocumentoStartingWithAndEstadoIsTrue
                        (companyEntity.getId(), client.getTipoDocumento(), client.getNumeroDocumento());
                if (listResult.size() > 0) {
                    return listResult.get(0);
                } else {
                    client.setCompany(companyEntity);
                    client.setCreatedBy(userResponse.getUsername());
                    client.setEstado(true);
                    return clientRepository.save(client);
                }

            }
        }
        return null;
    }

    @Override
    public void deleteClient(Long id, UserPrincipal userResponse) {
        ClientEntity clientEntity = clientRepository.findById(id.intValue()).get();
        clientEntity.setEstado(false);
        clientEntity.setUpdatedBy(userResponse.getUsername());
        clientRepository.save(clientEntity);
    }

    @Override
    public Map<String, Object> findByCompanyId(int pagenumber, int perpage, UserPrincipal user, String filter) {
        User usuario = userRepository.findById(user.getId()).get();
        CompanyEntity companyEntity = usuario.getCompany();
        if (filter.length() > 0) {
            long count = clientRepository.countSearchByCompanyIdAndEstadoIsTrue(companyEntity.getId(), filter);
            Page<ClientEntity> listP = clientRepository.findAllByCompanyIdAndFilterAndEstadoIsTrueOrderByNombreComercial(companyEntity.getId(), filter, new PageRequest(pagenumber - 1, perpage));
            return ImmutableMap.of("results", listP, "total_results", count);
        } else {
            Page<ClientEntity> listClients = clientRepository.findAllByCompanyIdAndEstadoIsTrueOrderByNombreComercial(companyEntity.getId(), new PageRequest(pagenumber - 1, perpage));
            long count = clientRepository.countByCompanyIdAndEstadoIsTrue(companyEntity.getId());
            return ImmutableMap.of("results", listClients, "total_results", count);
        }
    }

    @Override
    public Map<String, Object> valiteAll(List<ClientEntity> clients) {
        List<ClientEntity> clientes = clients;
        Integer estado = 200;
        List<ClientEntity> clientesObs = new ArrayList<>();
        List<String> strMnsags = new ArrayList<>();

        for (ClientEntity clientEntity : clientes) {
            if (((Boolean) (validateEntity(clientEntity)).get("status")).booleanValue()) {
                clientesObs.add(clientEntity);
                strMnsags.add((String.valueOf((validateEntity(clientEntity)).get("mensag"))).toUpperCase());
            }
        }

        if (clientesObs.size() > 0) {
            estado = 500;
        }
        return ImmutableMap.of("listProductosObs", clientesObs, "Obs ", strMnsags, "status", estado);
    }

    private Map<String, Object> validateEntity(ClientEntity clientEntity) {

        boolean res = false;
        String mnsag = "";
        if (Integer.parseInt(clientEntity.getTipoDocumento().toString()) > 15) {
            mnsag += " Campo tipo de documento incorrecto";
            res = true;
        }
        if (clientEntity.getNumeroDocumento().length() > 50) {
            mnsag += " Campo numero de documento extenso";
            res = true;
        }
        if (clientEntity.getRazonSocial().toString() == "") {
            mnsag += " Campo razon social vacio";
            res = true;
        }

        return ImmutableMap.of("mensag", mnsag, "status", res);

    }

    @Override
    public void saveAll(List<ClientEntity> clients, UserPrincipal user) {

        User usuario = userRepository.findById(user.getId()).get();

        for (ClientEntity client : new ArrayList<>(clients)) {
            client.setCreatedBy(user.getUsername());
            client.setCompany(usuario.getCompany());
            client.setEstado(true);
            client.setEmail(client.getEmail().replace("&",","));
            ClientEntity clientEntity = clientRepository.findByCompanyIdAndTipoDocumentoAndNumeroDocumentoAndEstadoIsTrue(client.getCompany().getId(), client.getTipoDocumento(), client.getNumeroDocumento());
            if (clientEntity != null) {
                clients.remove(client);
                if (!clientEntity.isEstado()) {
                    clientEntity.setEstado(true);
                    clientRepository.save(clientEntity);
                }
            }
        }
        clientRepository.saveAll(clients);
    }
}
