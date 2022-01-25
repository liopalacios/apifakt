package pe.com.certifakt.apifact.repository;

import org.springframework.data.repository.CrudRepository;
import pe.com.certifakt.apifact.model.WebhooksPaypanEntity;

public interface WebhooksPaypalRepository extends CrudRepository<WebhooksPaypanEntity, Long> {
}
