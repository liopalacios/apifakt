package pe.com.certifakt.apifact.service.impl;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pe.com.certifakt.apifact.model.ParameterEntity;
import pe.com.certifakt.apifact.repository.ParameterRepository;
import pe.com.certifakt.apifact.service.ParameterService;
import pe.com.certifakt.apifact.util.Parameters;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ParameterServiceImpl implements ParameterService {

    private final ParameterRepository parameterRepository;

    public ParameterEntity findByName(String name) {
        return parameterRepository.findByName(name);
    }

    @Override
    public List<Map<String, Object>> getParametersList() {
        ParameterEntity parameterIGV = parameterRepository.findByName(Parameters.PARAM_IGV);

        return Arrays.asList(ImmutableMap.of("key", "IGV", "value", parameterIGV.getValue()));
    }

}
