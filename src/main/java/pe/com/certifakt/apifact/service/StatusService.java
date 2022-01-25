package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.bean.ResponsePSE;

public interface StatusService {

    public ResponsePSE getStatus(String numeroTicket, String tipoResumen,
                                 String userName, String rucEmisor);

    public String getEstadoDocumentoResumenInBD(String tipoDocumento, String numeroTicket);

}
