package pe.com.certifakt.apifact.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.ProductEntity;

import java.util.List;


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findAllByCompanyIdAndEstadoIsTrueOrderByCreatedOnDesc(Integer id);

    @Query("select p from ProductEntity as p inner join p.company as c where c.id = :id and p.estado = true and ( lower(p.codigo) like lower(concat('%', :codigo,'%')) or lower(p.descripcion) like lower(concat('%', :descripcion,'%')))")
    List<ProductEntity> searchProductos(@Param("id") Integer id, @Param("codigo") String codigo, @Param("descripcion") String descripcion);

    @Query("select p from ProductEntity as p inner join p.company as c where c.id = :id and p.estado = true and ( lower(p.descripcion) like lower(concat('%', :descripcion,'%')) or lower(p.codigo) like lower(concat('%', :descripcion,'%')))")
    Page<ProductEntity> searchProductosOrderByIdDesc(@Param("id") Integer id, @Param("descripcion") String descripcion, Pageable pageable);

    Page<ProductEntity> findAllByCompanyIdAndEstadoIsTrueOrderByIdDesc(int companyEntity, Pageable pageable);

    @Query("select count(p) from ProductEntity as p inner join p.company as c where c.id = :id and p.estado = true and ( lower(p.descripcion) like lower(concat('%', :descripcion,'%')) or lower(p.codigo) like lower(concat('%', :descripcion,'%')))")
    long countSearchByCompanyIdAndEstadoIsTrue(@Param("id") Integer id, @Param("descripcion") String descripcion);

    long countByCompanyIdAndEstadoIsTrue(Integer id);

    ProductEntity findByCompanyIdAndId(Integer id, Long id1);

    ProductEntity findByCompanyIdAndCodigo(Integer id, String codigo);
}
