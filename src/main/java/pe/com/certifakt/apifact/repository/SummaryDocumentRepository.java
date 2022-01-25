package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.com.certifakt.apifact.model.SummaryDocumentEntity;

import java.sql.Timestamp;
import java.util.List;

public interface SummaryDocumentRepository extends JpaRepository<SummaryDocumentEntity, Long> {

    @Query("select coalesce(MAX(p.correlativoDia), 0) from SummaryDocumentEntity p "
            + "where p.rucEmisor = :rucEmisor "
            + "and p.fechaEmision = :fechaEmision ")
    public Integer getCorrelativoDiaByFechaEmisionInSummaryDocuments(
            @Param("rucEmisor") String rucEmisor,
            @Param("fechaEmision") String fechaEmision);

    @Query("select estado from SummaryDocumentEntity s "
            + "where s.ticketSunat = :numeroTicket")
    public String getEstadoByNumeroTicket(
            @Param("numeroTicket") String numeroTicket);

    @Query("select rucEmisor, estado from SummaryDocumentEntity s "
            + "where s.ticketSunat = :numeroTicket")
    public List<Object[]> getEstadoAndRucEmisorByNumeroTicket(
            @Param("numeroTicket") String numeroTicket);

    @Modifying(clearAutomatically = true)
    @Query("update SummaryDocumentEntity s "
            + "set s.estado = :estadoSummary, "
            + "s.codigoRespuesta = :codeResponse, "
            + "s.descripcionRespuesta = :description, "
            + "s.userNameModify = :usuario, "
            + "s.fechaModificacion = :fechaModificacion, "
            + "s.estadoComprobante = :estadoComprobante "
            + "where s.ticketSunat = :numeroTicket")
    public void actualizaEstadoSummaryFromGetStatus(
            @Param("numeroTicket") String numeroTicket,
            @Param("estadoSummary") String estadoSummary,
            @Param("codeResponse") String codeResponse,
            @Param("description") String description,
            @Param("usuario") String usuario,
            @Param("fechaModificacion") Timestamp fechaModificacion,
            @Param("estadoComprobante") String estadoComprobante);

    @Query("select s from SummaryDocumentEntity s "
            + "where s.ticketSunat = :numeroTicket")
    public SummaryDocumentEntity getSummaryByTicket(
            @Param("numeroTicket") String numeroTicket);

    /*
    @Query("select p.ticketSunat from SummaryDocumentEntity p "
            + "where p.fechaGeneracion = :fechaGenerada "
            + "and p.codigoRespuesta is null ")
    public List<String> getTicketsSinVerificacionByFechaGeneracionSummary(
            @Param("fechaGenerada") String fechaGenerada);
    */
    @Query("select p from SummaryDocumentEntity p "
            + "where p.fechaGeneracion = :fechaGeneracion "
            + "order by p.idDocument")
    public List<SummaryDocumentEntity> getSummariesByFechaGeneracion(
            @Param("fechaGeneracion") String fechaGeneracion);
/*
	@Query(value = "select s.ticket_sunat from summary_documents s "
			+ "where s.code_response is null "
			+ "order by s.fecha_generacion_resumen asc "
			+ "limit 1", nativeQuery=true)
	public String getTicketSummaryPendienteToConsultar();
	*/

    @Query("select p from SummaryDocumentEntity p "
            + "where p.fechaEmision = :fechaEmision "
            + "and p.estado='98' "
            + "order by p.idDocument")
    public List<SummaryDocumentEntity> getSummariesByFechaGeneracionPendientes(
            @Param("fechaEmision") String fechaEmision);


    @Query("select p from SummaryDocumentEntity p "
            + "where p.estado='98' "
            + "order by p.idDocument")
    public List<SummaryDocumentEntity> getSummariesByGeneracionPendientes();

    @Query("select p from SummaryDocumentEntity p "
            + "where p.idDocumentSummary = :idSummary ")
    SummaryDocumentEntity getSummaryByIddocumentsummary(@Param("idSummary") Long idSummary);
}
