package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.VoidedDocumentsEntity;

import java.sql.Timestamp;
import java.util.List;

public interface DocumentsVoidedRepository extends JpaRepository<VoidedDocumentsEntity, Long> {

    VoidedDocumentsEntity findByTicketSunat(String ticket);

    @Query("select estado from VoidedDocumentsEntity v "
            + "where v.ticketSunat = :numeroTicket")
    public String getEstadoByNumeroTicket(@Param("numeroTicket") String numeroTicket);

    @Modifying(clearAutomatically = true)
    @Query("update VoidedDocumentsEntity v "
            + "set v.estado = :estado, "
            + "v.codigoRespuesta = :codeResponse, "
            + "v.descripcionRespuesta = :description, "
            + "v.userNameModify = :usuario, "
            + "v.fechaModificacion = :fechaModificacion, "
            + "v.estadoComprobante = :estadoComprobante "
            + "where v.ticketSunat = :numeroTicket")
    public void actualizaEstadoVoidedFromGetStatus(
            @Param("numeroTicket") String numeroTicket,
            @Param("estado") String estado,
            @Param("codeResponse") String codeResponse,
            @Param("description") String description,
            @Param("usuario") String usuario,
            @Param("fechaModificacion") Timestamp fechaModificacion,
            @Param("estadoComprobante") String estadoComprobante);

    @Query("select v from VoidedDocumentsEntity v "
            + "where v.ticketSunat = :numeroTicket")
    public VoidedDocumentsEntity getVoidedByTicket(
            @Param("numeroTicket") String numeroTicket);

    @Query("select coalesce(MAX(p.correlativoGeneracionDia), 0) from VoidedDocumentsEntity p "
            + "where p.rucEmisor = :ruc "
            + "and p.fechaGeneracionBaja = :fechaGeneracionBaja ")
    public Integer getCorrelativoGeneracionByDiaInVoidedDocuments(
            @Param("ruc") String ruc,
            @Param("fechaGeneracionBaja") String fechaGeneracionBaja);


    @Query("select v from VoidedDocumentsEntity v "
            + "where v.fechaBajaDocs= :fechaBajaDocs "
            + "and v.estado='98' ")
    List<VoidedDocumentsEntity> getVoidedPendientesByFechaBajaDoc(
            @Param("fechaBajaDocs") String fechaBajaDocs);


    @Query("select v from VoidedDocumentsEntity v "
            + "where v.estado='98' "
            + "order by v.idDocumentVoided")
    List<VoidedDocumentsEntity> getVoidedPendientes();

}
