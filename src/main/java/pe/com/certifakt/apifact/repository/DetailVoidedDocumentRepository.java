package pe.com.certifakt.apifact.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.com.certifakt.apifact.model.DetailDocsVoidedEntity;

public interface DetailVoidedDocumentRepository extends JpaRepository<DetailDocsVoidedEntity, Long> {

    DetailDocsVoidedEntity findFirst1ByVoidedDocument_RucEmisorAndTipoComprobanteAndSerieDocumentoAndNumeroDocumento(String ruc, String tipo, String serie, Integer numero);

}
