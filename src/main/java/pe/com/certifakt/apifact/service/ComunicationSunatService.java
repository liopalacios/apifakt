package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.dto.GetStatusCdrDTO;
import pe.com.certifakt.apifact.dto.GetStatusExcelDTO;
import pe.com.certifakt.apifact.dto.SendOtherDocumentDTO;

import java.io.ByteArrayInputStream;
import java.util.Map;

public interface ComunicationSunatService {

    public Map<String, Object> sendDocumentBill(String ruc, Long idPaymentVoucher);

    public Map<String, Object> sendOtrosCpe(SendOtherDocumentDTO otroCpe);

    public Map<String, Object> sendGuiaRemision(SendOtherDocumentDTO dataGuiaRemision);

    public Map<String, Object> getStatusBill(GetStatusCdrDTO getStatusCdr);

    public Map<String, Object> sendDocumentExcel(String ruc,Long idExcelDocument,ByteArrayInputStream stream) throws Exception;

}
