package pe.com.certifakt.apifact.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.bean.ChangePassword;
import pe.com.certifakt.apifact.enums.TipoUsuarioEnum;
import pe.com.certifakt.apifact.exception.ServiceException;
import pe.com.certifakt.apifact.model.*;
import pe.com.certifakt.apifact.repository.AuthorityRepository;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.repository.UserRepository;
import pe.com.certifakt.apifact.security.UserPrincipal;
import pe.com.certifakt.apifact.service.UserService;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(final User user) {

        Authority authorityRoleUser = authorityRepository.findByName(AuthorityName.ROLE_USER);

        CompanyEntity companyEntity = user.getCompany();

        //VALIDANDO EMPRESA
        CompanyEntity companyEntityExiste = companyRepository.findByRuc(companyEntity.getRuc());
        if (companyEntityExiste != null) {
            throw new ServiceException("Ya esta registrado este número de RUC.");
        }

        companyEntity.setBucket(companyEntity.getRuc());
        companyEntity.setEstado("A");
        companyEntity.setSendticket(0);
        companyEntity.setEnvioAutomaticoSunat(false);
        companyEntity.setPreciosIncluidoIgv(false);
        companyEntity = companyRepository.save(companyEntity);
        user.setCompany(companyEntity);
        user.setAuthorities(Arrays.asList(authorityRoleUser));
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPasswordTemp()));
        user.setTypeUser(TipoUsuarioEnum.ADMIN.getCodigoTipoUsuario());
        user.setEstado(true);

        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public List<User> findAllByCompanyId(String ruc) {
        CompanyEntity companyEntity = companyRepository.findByRuc(ruc);
        return userRepository.findAllByCompanyIdAndEstadoIsTrue(companyEntity.getId());
    }

    @Override
    public User saveUser(User usuario, boolean isEdit, UserPrincipal userRegister) {


        if (isEdit) {
            User userEntity = userRepository.findById(usuario.getId()).get();
            userEntity.setTypeUser(usuario.getTypeUser());
            userEntity.setFullName(usuario.getFullName());
            userEntity.setDni(usuario.getDni());
            userEntity.setEnabled(usuario.getEnabled());
            return userRepository.save(userEntity);
        } else {
            Authority authorityRoleUser = authorityRepository.findByName(AuthorityName.ROLE_USER);
            CompanyEntity companyEntity = companyRepository.findByRuc(userRegister.getRuc());
            usuario.setCompany(companyEntity);
            usuario.setAuthorities(Arrays.asList(authorityRoleUser));
            usuario.setEnabled(true);
            usuario.setUsername(usuario.getUsername().trim());
            usuario.setPassword(passwordEncoder.encode(usuario.getDni()));
            usuario.setEstado(true);
            return userRepository.save(usuario);
        }

    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).get();
        user.setEstado(false);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void asignarOficinaUser(Long idUser, Integer idOficina) {
        User user = userRepository.findById(idUser).get();
        if (idOficina != null) {
            user.setOficina(BranchOfficeEntity.builder().id(idOficina).build());
        } else user.setOficina(null);

        userRepository.save(user);
    }

    @Override
    public BranchOfficeEntity getOfficeFromUser(Long id) {
        User user = userRepository.findById(id).get();
        BranchOfficeEntity ofi = user.getOficina();
        if (ofi != null && ofi.isEstado())
            return ofi;
        else return null;
    }

    @Override
    public Boolean changePass(Long idUser, ChangePassword changePassword) {

        User userDoc = userRepository.findById(idUser).get();
        Boolean passwordModel = passwordEncoder.matches(changePassword.getPassword(), userDoc.getPassword());

        if (!passwordModel)
            throw new ServiceException("La contraseña actual ingresada no es correcta.");

        if (changePassword.getPassword().isEmpty() || changePassword.getNewPassword().isEmpty() || changePassword.getConfirmPassword().isEmpty())
            throw new ServiceException("Llene todos los campos requeridos.");

        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword()))
            throw new ServiceException("La nueva contraseña no coincide con la confirmación.");

        userDoc.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userRepository.save(userDoc);

        return true;
    }

}
