package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.bean.RazonSocial;

public interface SunatService {

    RazonSocial findRazonSocialByRUC(String ruc);

    String findNombreByDNI(String dni);

}
