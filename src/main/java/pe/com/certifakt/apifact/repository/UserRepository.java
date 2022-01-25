package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.com.certifakt.apifact.dto.inter.UserInterDto;
import pe.com.certifakt.apifact.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query(value ="select u.id_user as id, u.oficina_id as idOficina from users u " +
             " where u.de_login = ?1 ", nativeQuery = true)
    Optional<UserInterDto> findByUsernameInter(String username);

    List<User> findAllByCompanyIdAndEstadoIsTrue(Integer id);
}
