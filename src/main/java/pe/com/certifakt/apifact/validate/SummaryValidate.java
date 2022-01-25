package pe.com.certifakt.apifact.validate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.exception.ValidatorFieldsException;
import pe.com.certifakt.apifact.repository.CompanyRepository;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.FieldsInput;
import pe.com.certifakt.apifact.util.UtilFormat;

import java.io.IOException;

@Component
public class SummaryValidate extends FieldsInput<Object> {

    @Autowired
    private CompanyRepository companyRepository;

    public void validateSummaryByFechaEmision(String rucEmisor, String fechaEmision) throws ValidatorFieldsException {

        validateRucActivo(rucEmisor);
        validateFechaEmision(fechaEmision);
    }

    private void validateRucActivo(String rucEmisor) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        String estado = companyRepository.getStateCompanyByRuc(rucEmisor);
        if (estado!=null){
            if (!estado.equals(ConstantesParameter.REGISTRO_ACTIVO)) {
                mensajeValidacion = "El ruc emisor [" + rucEmisor + "] No se encuentra habilitado para "
                        + "ejecutar operaciones al API-REST.";
                System.out.println(mensajeValidacion);

                //throw new ValidatorFieldsException(mensajeValidacion);
            }
        }else {
            mensajeValidacion = "El ruc emisor [" + rucEmisor + "] No existe "
                    + "ejecutar operaciones al API-REST.";
            System.out.println(mensajeValidacion);
        }

    }

    private void validateFechaEmision(String fechaEmision) throws ValidatorFieldsException {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(fechaEmision)) {
            mensajeValidacion = "El campo [" + fechaEmisionLabel + "] es obligatorio.";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
        if (UtilFormat.fechaDate(fechaEmision) == null) {
            mensajeValidacion = "El campo [" + fechaEmisionLabel + "] debe tener el formato yyyy-MM-dd";
            throw new ValidatorFieldsException(mensajeValidacion);
        }
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // TODO Auto-generated method stub
        return null;
    }
}
