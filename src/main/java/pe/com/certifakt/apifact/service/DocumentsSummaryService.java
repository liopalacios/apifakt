package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.bean.IdentificadorComprobante;
import pe.com.certifakt.apifact.bean.ResponsePSE;
import pe.com.certifakt.apifact.bean.Summary;
import pe.com.certifakt.apifact.bean.SummaryResponse;

import java.util.List;

public interface DocumentsSummaryService {

    void registrarSummaryDocuments(Summary summary, Long idRegisterFile, String usuario, String ticket, List<Long> ids);

    ResponsePSE generarSummaryByFechaEmisionAndRuc(String ruc, String fechaEmision, IdentificadorComprobante comprobante, String usuario);

    ResponsePSE generarSummaryNotaCreditoByFechaEmisionAndRuc(String ruc, String fechaEmision, IdentificadorComprobante comprobante, String usuario);

    List<SummaryResponse> listarSummariesByFechaGeneracion(String fechaGeneracion);

    ResponsePSE processSummaryTicket(String ticket, String useName, String rucEmisor);

    List<String> getFechasPendientesNotas();

    List<String> getRucsPendientesNotas();
}
