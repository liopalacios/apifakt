package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.com.certifakt.apifact.model.CatalogSunatEntity;
import pe.com.certifakt.apifact.model.DetailsPaymentVoucherEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogSunatDto {
    private String numero;
    private String codigo;
    private String descripcion;
    private String codigoRelacionado;


    public static CatalogSunatDto transformToDto(CatalogSunatEntity model){
        if (model == null) return null;
        return CatalogSunatDto.builder()
                .codigo(model.getCodigo())
                .descripcion(model.getDescripcion())
                .codigoRelacionado(model.getCodigoRelacionado())
                .numero(model.getNumero()).build();

    }
    public static List<CatalogSunatDto> transformToDtoList(List<CatalogSunatEntity> models){
        if (models == null) return Collections.emptyList();
        return models.stream().map(CatalogSunatDto::transformToDto).collect(Collectors.toList());
    }
}
