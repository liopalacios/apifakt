package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.bean.ChangePassword;
import pe.com.certifakt.apifact.model.BranchOfficeEntity;
import pe.com.certifakt.apifact.model.User;
import pe.com.certifakt.apifact.security.UserPrincipal;

import java.util.List;

public interface UserService {

    User registerUser(User user);

    User findById(Long id);

    List<User> findAllByCompanyId(String ruc);

    User saveUser(User nuevoUsuario, boolean isEdit, UserPrincipal userRegister);

    void deleteUser(Long id);

    void asignarOficinaUser(Long idUser, Integer idOficina);

    BranchOfficeEntity getOfficeFromUser(Long id);

    Boolean changePass(Long idUser, ChangePassword changePassword);

}
