package pe.com.certifakt.apifact.bean;

import pe.com.certifakt.apifact.model.CompanyEntity;

import java.util.List;

public class TipoCampo {

    private Long id;

    private String name;

    private List<CampoAdicional> AditionalFields;

    private List<CompanyEntity> companys;

}
