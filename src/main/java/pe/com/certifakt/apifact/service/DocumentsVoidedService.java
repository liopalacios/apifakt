package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.bean.Comprobante;
import pe.com.certifakt.apifact.bean.ResponsePSE;
import pe.com.certifakt.apifact.bean.Voided;
import pe.com.certifakt.apifact.bean.VoucherAnnular;
import pe.com.certifakt.apifact.model.VoidedDocumentsEntity;

import java.util.List;

public interface DocumentsVoidedService {

    VoidedDocumentsEntity registrarVoidedDocuments(Voided voided, Long idRegisterFile, String usuario, String ticket);

    String obtenerEstadoSummaryByNumeroTicket(String numeroTicket);

    List<Comprobante> listarIdentificadorDocumentoByTicket(String ticket);

    Boolean processVoidedTicket(String ticket, String useName, String rucEmisor);

    ResponsePSE anularDocuments(List<VoucherAnnular> documents, String rucEmisor, String userName, List<String> ticketsVoidedProcess);

    ResponsePSE voidedTicket(String ticket, String useName, String rucEmisor);

}
