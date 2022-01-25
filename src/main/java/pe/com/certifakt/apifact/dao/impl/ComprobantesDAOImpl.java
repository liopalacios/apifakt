package pe.com.certifakt.apifact.dao.impl;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pe.com.certifakt.apifact.bean.SearchCriteria;
import pe.com.certifakt.apifact.dao.ComprobantesDAO;
import pe.com.certifakt.apifact.enums.EstadoSunatEnum;
import pe.com.certifakt.apifact.model.DetailDocsVoidedEntity;
import pe.com.certifakt.apifact.model.PaymentVoucherEntity;
import pe.com.certifakt.apifact.repository.DetailVoidedDocumentRepository;
import pe.com.certifakt.apifact.util.ConstantesParameter;
import pe.com.certifakt.apifact.util.ConstantesUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class ComprobantesDAOImpl implements ComprobantesDAO {


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DetailVoidedDocumentRepository detailVoidedDocumentRepository;


    @Override
    public Map<String, Object> buscarComprobantes(List<SearchCriteria> searchCriteriaList, Integer pageNumber, Integer perPage) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PaymentVoucherEntity> query = builder.createQuery(PaymentVoucherEntity.class);
        CriteriaQuery<Long> queryTotal = builder.createQuery(Long.class);
        Root<PaymentVoucherEntity> from = query.from(PaymentVoucherEntity.class);

        Predicate predicate = builder.conjunction();

        for (SearchCriteria param : searchCriteriaList) {
            if (param.getOperation().equalsIgnoreCase(">")) {
                predicate = builder.and(predicate,
                        builder.greaterThanOrEqualTo(from.get(param.getKey()),
                                (Date) param.getValue()));
            } else if (param.getOperation().equalsIgnoreCase("<")) {
                predicate = builder.and(predicate,
                        builder.lessThanOrEqualTo(from.get(param.getKey()),
                                (Date) param.getValue()));
            } else if (param.getOperation().equalsIgnoreCase(":")) {
                if (from.get(param.getKey()).getJavaType() == String.class) {
                    predicate = builder.and(predicate,
                            builder.like(from.get(param.getKey()),
                                    "%" + param.getValue() + "%"));
                } else {
                    predicate = builder.and(predicate,
                            builder.equal(from.get(param.getKey()), param.getValue()));
                }
            }
        }

        //QUERY LIST
        query.where(predicate).orderBy(builder.desc(from.get("fechaRegistro")));
        TypedQuery<PaymentVoucherEntity> typedQuery = entityManager.createQuery(query);
        if (pageNumber != null)
            typedQuery.setFirstResult((pageNumber - 1) * perPage);
        if (perPage != null)
            typedQuery.setMaxResults(perPage);
        List<PaymentVoucherEntity> result = typedQuery.getResultList();

        result.forEach(pv -> {
            if (pv.getEstadoSunat() != null && pv.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado()) && pv.getTipoComprobante().equals(ConstantesUtils.TIPO_FACTURA)) {
                DetailDocsVoidedEntity detalleVoided = detailVoidedDocumentRepository.findFirst1ByVoidedDocument_RucEmisorAndTipoComprobanteAndSerieDocumentoAndNumeroDocumento(pv.getRucEmisor(), pv.getTipoComprobante(), pv.getSerie(), pv.getNumero());
                if (detalleVoided != null) {
                    pv.setIdentificadorBaja(detalleVoided.getVoidedDocument().getIdDocument());
                }

            }
        });

        //QUERY TOTAL
        queryTotal.select(builder.count(queryTotal.from(PaymentVoucherEntity.class)));
        queryTotal.where(predicate);
        TypedQuery<Long> typedQueryTotal = entityManager.createQuery(queryTotal);
        long total = typedQueryTotal.getSingleResult();

        return ImmutableMap.of("comprobantesList", result, "total", total);
    }

    @Override
    public List<PaymentVoucherEntity> buscarComprobantes(List<SearchCriteria> searchCriteriaList) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PaymentVoucherEntity> query = builder.createQuery(PaymentVoucherEntity.class);
        Root<PaymentVoucherEntity> root = query.from(PaymentVoucherEntity.class);

        Predicate predicate = builder.conjunction();

        for (SearchCriteria param : searchCriteriaList) {

            switch(param.getOperation()) {

                case ConstantesParameter.OPERADOR_IGUAL:
                    predicate = builder.and(predicate, builder.equal(root.get(param.getKey()),
                            param.getValue()));
                    break;
                case ConstantesParameter.OPERADOR_MAYOR_IGUAL:
                    predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(param.getKey()),
                            (Date) param.getValue()));
                    break;
                case ConstantesParameter.OPERADOR_MENOR_IGUAL:
                    predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get(param.getKey()),
                            (Date) param.getValue()));
                    break;
                case ConstantesParameter.OPERADOR_LIKE:
                    predicate = builder.and(predicate, builder.like(root.get(param.getKey()),
                            "%" + param.getValue() + "%"));
            }
        }
        query.where(predicate);

        List<PaymentVoucherEntity> result = entityManager.createQuery(query).getResultList();
        return result;
    }

    @Override
    public Map<String, Object> buscarAllComprobantes(List<SearchCriteria> searchCriteriaList) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PaymentVoucherEntity> query = builder.createQuery(PaymentVoucherEntity.class);
        CriteriaQuery<Long> queryTotal = builder.createQuery(Long.class);
        Root<PaymentVoucherEntity> from = query.from(PaymentVoucherEntity.class);

        Predicate predicate = builder.conjunction();

        for (SearchCriteria param : searchCriteriaList) {
            if (param.getOperation().equalsIgnoreCase(">")) {
                predicate = builder.and(predicate,
                        builder.greaterThanOrEqualTo(from.get(param.getKey()),
                                (Date) param.getValue()));
            } else if (param.getOperation().equalsIgnoreCase("<")) {
                predicate = builder.and(predicate,
                        builder.lessThanOrEqualTo(from.get(param.getKey()),
                                (Date) param.getValue()));
            } else if (param.getOperation().equalsIgnoreCase(":")) {
                if (from.get(param.getKey()).getJavaType() == String.class) {
                    predicate = builder.and(predicate,
                            builder.like(from.get(param.getKey()),
                                    "%" + param.getValue() + "%"));
                } else {
                    predicate = builder.and(predicate,
                            builder.equal(from.get(param.getKey()), param.getValue()));
                }
            }
        }

        //QUERY LIST
        query.where(predicate).orderBy(builder.desc(from.get("fechaRegistro")));
        TypedQuery<PaymentVoucherEntity> typedQuery = entityManager.createQuery(query);

        List<PaymentVoucherEntity> result = typedQuery.getResultList();

        result.forEach(pv -> {
            if (pv.getEstadoSunat() != null && pv.getEstadoSunat().equals(EstadoSunatEnum.ANULADO.getAbreviado()) && pv.getTipoComprobante().equals(ConstantesUtils.TIPO_FACTURA)) {
                DetailDocsVoidedEntity detalleVoided = detailVoidedDocumentRepository.findFirst1ByVoidedDocument_RucEmisorAndTipoComprobanteAndSerieDocumentoAndNumeroDocumento(pv.getRucEmisor(), pv.getTipoComprobante(), pv.getSerie(), pv.getNumero());
                if (detalleVoided != null) {
                    pv.setIdentificadorBaja(detalleVoided.getVoidedDocument().getIdDocument());
                }

            }
        });

        //QUERY TOTAL
        queryTotal.select(builder.count(queryTotal.from(PaymentVoucherEntity.class)));
        queryTotal.where(predicate);
        TypedQuery<Long> typedQueryTotal = entityManager.createQuery(queryTotal);
        long total = typedQueryTotal.getSingleResult();

        return ImmutableMap.of("comprobantesList", result, "total", total);
    }
}
