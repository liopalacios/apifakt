package pe.com.certifakt.apifact.service;

import pe.com.certifakt.apifact.model.ParameterEntity;

import java.util.List;
import java.util.Map;

public interface ParameterService {
    public ParameterEntity findByName(String name);

    List<Map<String, Object>> getParametersList();
}
